package com.alami.dpd;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class InstallmentLoanV3 {
    private List<InstallmentV3> installments;
    private Status status;

    public Dpd calculateLatestDpd(LocalDate calculationDate) {
        // If loan is fully repaid, return 0 for latest DPD
        if (status == Status.REPAYMENT_SUCCESS || status == Status.SETTLEMENT_SUCCESS) {
            return Dpd.builder()
                    .latestDpd(0)
                    .maxDpd(calculateMaxDpd(calculationDate))
                    .build();
        }

        // For written off loans, calculate DPD from latest maturity date to write-off date
        if (status == Status.WRITE_OFF) {
            Optional<InstallmentV3> writtenOffInstallment = installments.stream()
                    .filter(InstallmentV3::isWrittenOff)
                    .findFirst();

            if (writtenOffInstallment.isPresent() && writtenOffInstallment.get().getWrittenOfDate() != null) {
                // Find latest maturity date from non-grace periods
                LocalDate latestMaturityDate = installments.stream()
                        .filter(installment -> !installment.isGracePeriod() && installment.getMaturityDate() != null)
                        .map(InstallmentV3::getMaturityDate)
                        .max(LocalDate::compareTo)
                        .orElse(calculationDate);

                LocalDate writtenOffDate = writtenOffInstallment.get().getWrittenOfDate();
                System.out.println("writtenof date:" + writtenOffDate);
                System.out.println("latestMaturityDate:" + latestMaturityDate);
                long dpd = java.time.temporal.ChronoUnit.DAYS.between(latestMaturityDate, writtenOffDate);
                return Dpd.builder()
                        .latestDpd((int) dpd)
                        .maxDpd((int) dpd)
                        .build();
            }
        }

        // Get the earliest maturity date
        LocalDate earliestMaturityDate = installments.stream()
                .filter(installment -> !installment.isGracePeriod() && installment.getMaturityDate() != null)
                .map(InstallmentV3::getMaturityDate)
                .min(LocalDate::compareTo)
                .orElse(calculationDate);

        // If calculation date is before or equal to earliest maturity date, return 0
        if (calculationDate.compareTo(earliestMaturityDate) <= 0) {
            return Dpd.builder()
                    .latestDpd(0)
                    .maxDpd(0)
                    .build();
        }

        // For latest DPD, find the earliest period that is not fully paid
        Optional<InstallmentDpd> latestPeriodWithDpd = installments.stream()
                .filter(installment -> !installment.isGracePeriod())
                .filter(installment -> !installment.isFullyPaid() || installment.isPartiallyPaid())
                .min((a, b) -> a.getMaturityDate().compareTo(b.getMaturityDate()))
                .map(installment -> new InstallmentDpd(
                        installment.calculateDpd(calculationDate),
                        installment.isFullyPaid(),
                        installment.getMaturityDate() != null ? installment.getMaturityDate() : earliestMaturityDate,
                        installment.getRepaymentDate(),
                        installment.isPartiallyPaid(),
                        false,
                        null
                ));

        long latestDpd = latestPeriodWithDpd
                .map(InstallmentDpd::getDpd)
                .orElse(0L);

        return Dpd.builder()
                .latestDpd((int) latestDpd)
                .maxDpd(calculateMaxDpd(calculationDate))
                .build();
    }

    private int calculateMaxDpd(LocalDate calculationDate) {
        return installments.stream()
                .filter(installment -> !installment.isGracePeriod())
                .mapToInt(installment -> (int) installment.calculateDpd(calculationDate))
                .max()
                .orElse(0);
    }

    public long calculateMaxHistoricalDpd(List<LocalDate> historicalDates) {
        return historicalDates.stream()
                .map(this::calculateLatestDpd)
                .mapToInt(Dpd::getMaxDpd)
                .max()
                .orElse(0);
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
