package com.alami.dpd;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Installment {
    public Dpd calculateV4(List<InstallmentLoan> installmentLoans, LocalDate today) {
        if (installmentLoans == null || installmentLoans.isEmpty()) {
            return new Dpd(0, 0);
        }

        // Find earliest unpaid installment (no payment amount) by maturity date
        InstallmentLoan earliestUnpaid = installmentLoans.stream()
            .filter(loan -> loan.getBenefPaymentAmount() == null)
            .min((a, b) -> a.getMaturityDate().compareTo(b.getMaturityDate()))
            .orElse(null);

        // If all installments are paid or we're before first maturity
        if (earliestUnpaid == null || today.isBefore(earliestUnpaid.getMaturityDate())) {
            return new Dpd(0, 0);
        }

        // Calculate DPD based on earliest unpaid installment
        int dpd = (int) ChronoUnit.DAYS.between(earliestUnpaid.getMaturityDate(), today);
        
        return new Dpd(dpd, dpd); // Both latest and max DPD are the same
    }
}
