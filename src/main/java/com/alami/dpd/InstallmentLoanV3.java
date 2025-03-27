package com.alami.dpd;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class InstallmentLoanV3 {
    private List<InstallmentV3> installments;
    private Status status;

    public Dpd calculateLatestDpd(LocalDate calculationDate) {
        if (status == Status.WRITE_OFF) {
            return calculateWrittenOffDpd(calculationDate);
        }

        LocalDate earliestMaturityDate = findEarliestMaturityDate(calculationDate);
        if (calculationDate.compareTo(earliestMaturityDate) <= 0) {
            return buildDpd(0, 0);
        }

        int latestDpd = findLatestDpd(calculationDate, earliestMaturityDate);
        int maxDpd = calculateMaxDpd(calculationDate);
        return buildDpd(latestDpd, maxDpd);
    }

    private Dpd calculateWrittenOffDpd(LocalDate calculationDate) {
        Optional<InstallmentV3> writtenOffInstallment = findWrittenOffInstallment();
        if (writtenOffInstallment.isEmpty() || writtenOffInstallment.get().getWrittenOfDate() == null) {
            return buildDpd(0, calculateMaxDpd(calculationDate));
        }

        int dpd = calculateWrittenOffDpdValue(writtenOffInstallment.get(), calculationDate);
        return buildDpd(dpd, dpd);
    }

    private Optional<InstallmentV3> findWrittenOffInstallment() {
        return installments.stream()
                .filter(InstallmentV3::isWrittenOff)
                .findFirst();
    }

    private int calculateWrittenOffDpdValue(InstallmentV3 writtenOffInstallment, LocalDate calculationDate) {
        LocalDate latestMaturityDate = findLatestMaturityDateForNonGracePeriods(calculationDate);
        LocalDate writtenOffDate = writtenOffInstallment.getWrittenOfDate();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(latestMaturityDate, writtenOffDate);
    }

    private LocalDate findLatestMaturityDateForNonGracePeriods(LocalDate defaultDate) {
        return installments.stream()
                .filter(this::isValidNonGracePeriod)
                .map(InstallmentV3::getMaturityDate)
                .max(LocalDate::compareTo)
                .orElse(defaultDate);
    }

    private LocalDate findEarliestMaturityDate(LocalDate defaultDate) {
        return installments.stream()
                .filter(this::isValidNonGracePeriod)
                .map(InstallmentV3::getMaturityDate)
                .min(LocalDate::compareTo)
                .orElse(defaultDate);
    }

    private boolean isValidNonGracePeriod(InstallmentV3 installment) {
        return !installment.isGracePeriod() 
            && installment.getMaturityDate() != null
            && installment.getAmount() != null 
            && installment.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private int findLatestDpd(LocalDate calculationDate, LocalDate earliestMaturityDate) {
        InstallmentDpd latestPeriod = findLatestPeriodWithDpd(calculationDate, earliestMaturityDate);
        return latestPeriod != null ? (int) latestPeriod.getDpd() : 0;
    }

    private InstallmentDpd findLatestPeriodWithDpd(LocalDate calculationDate, LocalDate earliestMaturityDate) {
        var res = installments.stream()
                .filter(installment -> !installment.isGracePeriod())
                .collect(Collectors.groupingBy(InstallmentV3::getPeriod))
                .values().stream()
                .map(periodInstallments -> {
                    InstallmentV3 installment = periodInstallments.get(0);
                    Optional<InstallmentV3> paidInstallment = findFullyPaidInstallment(periodInstallments);
                    
                    if (paidInstallment.isPresent() && paidInstallment.get().getRepaymentDate() != null) {
                        LocalDate repaymentDate = paidInstallment.get().getRepaymentDate();
                        LocalDate maturityDate = paidInstallment.get().getMaturityDate();
                        
                        if (repaymentDate.isAfter(maturityDate)) {
                            // For late payments, calculate DPD from maturity to repayment date
                            return createInstallmentDpd(
                                paidInstallment.get(),
                                repaymentDate,
                                earliestMaturityDate
                            );
                        }
                        return null;
                    }
                    
                    if (!installment.isFullyPaid()) {
                        return createInstallmentDpd(
                            installment,
                            calculationDate,
                            earliestMaturityDate
                        );
                    }
                    
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(dpd -> dpd.getDpd() > 0)  // Only consider periods with DPD > 0
                .max((a, b) -> {
                    // Get the installments for both periods
                    InstallmentV3 installmentA = installments.stream()
                            .filter(i -> i.getMaturityDate().equals(a.getMaturityDate()))
                            .findFirst()
                            .orElse(null);
                    InstallmentV3 installmentB = installments.stream()
                            .filter(i -> i.getMaturityDate().equals(b.getMaturityDate()))
                            .findFirst()
                            .orElse(null);
                    
                    if (installmentA != null && installmentB != null) {
                        // If one is NOT_PAID and the other is PAID, prioritize NOT_PAID
                        boolean isNotPaidA = !installmentA.isFullyPaid();
                        boolean isNotPaidB = !installmentB.isFullyPaid();
                        if (isNotPaidA && !isNotPaidB) return 1;
                        if (!isNotPaidA && isNotPaidB) return -1;
                        
                        // If both are NOT_PAID, check if there are multiple NOT_PAID periods with DPD > 0
                        if (isNotPaidA && isNotPaidB) {
                            // Count how many NOT_PAID periods with DPD > 0
                            long notPaidCount = installments.stream()
                                    .filter(i -> !i.isFullyPaid())
                                    .filter(i -> {
                                        InstallmentDpd dpd = createInstallmentDpd(i, calculationDate, earliestMaturityDate);
                                        return dpd != null && dpd.getDpd() > 0;
                                    })
                                    .count();
                            
                            // If there are multiple NOT_PAID periods with DPD > 0, return highest DPD
                            // Otherwise, return its DPD
                            if (notPaidCount > 1) {
                                return Long.compare(a.getDpd(), b.getDpd());
                            } else {
                                return 0;
                            }
                        }
                        
                        // Otherwise (both are PAID or single NOT_PAID), return latest period
                        return Integer.compare(installmentA.getPeriod(), installmentB.getPeriod());
                    }
                    
                    // Fallback to maturity date comparison
                    return a.getMaturityDate().compareTo(b.getMaturityDate());
                })
                .orElse(null);

        return res;
    }

    private InstallmentDpd createInstallmentDpd(InstallmentV3 installment, LocalDate calculationDate, LocalDate earliestMaturityDate) {
        return new InstallmentDpd(
                installment.calculateDpd(calculationDate),
                installment.isFullyPaid(),
                installment.getMaturityDate() != null ? installment.getMaturityDate() : earliestMaturityDate,
                installment.getRepaymentDate(),
                installment.isPartiallyPaid(),
                false,
                null
        );
    }

    private int calculateMaxDpd(LocalDate calculationDate) {
        return installments.stream()
                .filter(this::isValidNonGracePeriod)
                .collect(Collectors.groupingBy(InstallmentV3::getPeriod))
                .values().stream()
                .map(periodInstallments -> calculatePeriodDpd(periodInstallments, calculationDate))
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    private int calculatePeriodDpd(List<InstallmentV3> periodInstallments, LocalDate calculationDate) {
        Optional<InstallmentV3> paidInstallment = findFullyPaidInstallment(periodInstallments);
        if (paidInstallment.isPresent() && paidInstallment.get().getRepaymentDate() != null) {
            return calculatePaidInstallmentDpd(periodInstallments.get(0), paidInstallment.get());
        }
        return (int) periodInstallments.get(0).calculateDpd(calculationDate);
    }

    private Optional<InstallmentV3> findFullyPaidInstallment(List<InstallmentV3> periodInstallments) {
        return periodInstallments.stream()
                .filter(InstallmentV3::isFullyPaid)
                .findFirst();
    }

    private int calculatePaidInstallmentDpd(InstallmentV3 firstInstallment, InstallmentV3 paidInstallment) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(
                firstInstallment.getMaturityDate(),
                paidInstallment.getRepaymentDate()
        );
    }

    private Dpd buildDpd(int latestDpd, int maxDpd) {
        return Dpd.builder()
                .latestDpd(latestDpd)
                .maxDpd(maxDpd)
                .build();
    }

    @Value
    private static class InstallmentDpd {
        long dpd;
        boolean paid;
        LocalDate maturityDate;
        LocalDate repaymentDate;
        boolean partiallyPaid;
        boolean writtenOff;
        LocalDate writtenOfDate;
    }
}
