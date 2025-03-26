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


    public Dpd calculate(List<InstallmentLoanV2> loans) {
        if (loans == null || loans.isEmpty()) {
            return Dpd.builder()
                     .latestDpd(0)
                     .maxDpd(0)
                     .build();
        }

        LocalDate currentDate = loans.get(0).getToday();

        // If we haven't reached first maturity date, no DPD
        if (currentDate.isBefore(loans.get(0).getMaturityDate())) {
            return Dpd.builder()
                     .latestDpd(0)
                     .maxDpd(0)
                     .build();
        }

        int maxDpd = 0;
        int latestDpd = 0;

        // Find the earliest unpaid period that's past due
        InstallmentLoanV2 earliestUnpaidPeriod = null;
        for (InstallmentLoanV2 loan : loans) {
            if (!isGracePeriod(loan) && !isInstallmentPeriodFullyPaid(loan) && currentDate.isAfter(loan.getMaturityDate())) {
                if (earliestUnpaidPeriod == null ||
                    loan.getMaturityDate().isBefore(earliestUnpaidPeriod.getMaturityDate())) {
                    earliestUnpaidPeriod = loan;
                }
            }
        }

        // If we found an unpaid period, calculate DPD from its maturity date
        if (earliestUnpaidPeriod != null) {
            int dpd = calculateDpd(earliestUnpaidPeriod.getMaturityDate(), currentDate);
            latestDpd = dpd;
            maxDpd = dpd;
        }

        return Dpd.builder()
                 .latestDpd(latestDpd)
                 .maxDpd(maxDpd)
                 .build();
    }
}