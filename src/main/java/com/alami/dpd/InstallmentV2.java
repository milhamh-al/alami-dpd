package com.alami.dpd;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InstallmentV2 {
    private int calculateDpd(LocalDate maturityDate, LocalDate currentDate) {
        return (int) ChronoUnit.DAYS.between(maturityDate, currentDate);
    }

    private boolean isGracePeriod(InstallmentLoanV2 loan) {
        return loan.getAmount().equals(BigDecimal.ZERO);
    }

    private boolean isInstallmentPeriodFullyPaid(InstallmentLoanV2 loan) {
        return loan.getRepaymentStatus() != RepaymentStatus.NOT_PAID &&
               loan.getRepaymentStatus() != RepaymentStatus.PARTIAL_REPAYMENT;
    }


    public Dpd calculate(List<InstallmentLoanV2> loans, LocalDate calculationDate) {
        if (loans == null || loans.isEmpty() || calculationDate == null) {
            return Dpd.builder()
                     .latestDpd(0)
                     .maxDpd(0)
                     .build();
        }

        // If we haven't reached first maturity date, no DPD
        if (calculationDate.isBefore(loans.get(0).getMaturityDate())) {
            return Dpd.builder()
                     .latestDpd(0)
                     .maxDpd(0)
                     .build();
        }

        int maxDpd = 0;
        int latestDpd = 0;

        // Find the earliest unpaid/partially paid period
        InstallmentLoanV2 earliestUnpaidPeriod = null;
        for (InstallmentLoanV2 loan : loans) {
            if (!isGracePeriod(loan) &&
                loan.getRepaymentStatus() != RepaymentStatus.PAID &&
                calculationDate.isAfter(loan.getMaturityDate())) {
                if (earliestUnpaidPeriod == null ||
                    loan.getMaturityDate().isBefore(earliestUnpaidPeriod.getMaturityDate())) {
                    earliestUnpaidPeriod = loan;
                }
            }
        }

        // Calculate max DPD from all periods
        for (InstallmentLoanV2 loan : loans) {
            if (!isGracePeriod(loan)) {
                LocalDate endDate;
                if (loan.getRepaymentStatus() == RepaymentStatus.PAID) {
                    endDate = loan.getRepaymentDate();
                } else {
                    endDate = calculationDate;
                }
                
                if (endDate.isAfter(loan.getMaturityDate())) {
                    int dpd = calculateDpd(loan.getMaturityDate(), endDate);
                    maxDpd = Math.max(maxDpd, dpd);
                }
            }
        }

        // Calculate latest DPD from the last period if not paid
        InstallmentLoanV2 lastPeriod = null;
        for (InstallmentLoanV2 loan : loans) {
            if (!isGracePeriod(loan)) {
                lastPeriod = loan;
            }
        }

        if (lastPeriod != null &&
            lastPeriod.getRepaymentStatus() != RepaymentStatus.PAID &&
            calculationDate.isAfter(lastPeriod.getMaturityDate())) {
            latestDpd = calculateDpd(lastPeriod.getMaturityDate(), calculationDate);
        }

        return Dpd.builder()
                 .latestDpd(latestDpd)
                 .maxDpd(maxDpd)
                 .build();
    }
}