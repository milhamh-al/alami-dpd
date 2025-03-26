package com.alami.dpd;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Installment {
    // Payment Status Checks
    private boolean isGracePeriod(InstallmentLoan loan) {
        return loan.getAmount().equals(BigDecimal.ZERO);
    }

    private boolean isRegularFullPayment(InstallmentLoan loan) {
        return !loan.getIsPartialInstallment()
            && loan.getBenefPaymentAmount() != null
            && loan.getBenefPaymentAmount().compareTo(loan.getAmount()) >= 0;
    }

    private LocalDate findLastMaturityDate(List<InstallmentLoan> loans) {
        return loans.stream()
            .map(InstallmentLoan::getMaturityDate)
            .max(LocalDate::compareTo)
            .orElse(null);
    }

    private boolean isAfterMaturity(InstallmentLoan loan) {
        return loan.getToday().isAfter(loan.getMaturityDate());
    }

    private boolean isInFinalPeriod(InstallmentLoan loan, List<InstallmentLoan> allLoans) {
        LocalDate lastMaturityDate = findLastMaturityDate(allLoans);
        return !loan.getToday().isBefore(lastMaturityDate);
    }

    private boolean isPeriodFullyPaid(InstallmentLoan loan, BigDecimal totalPayments) {
        return totalPayments.compareTo(loan.getAmount()) >= 0;
    }

    // Payment Calculations
    private BigDecimal calculateTotalPaymentsUpToDate(List<InstallmentLoan> loans, InstallmentLoan currentLoan) {
        return loans.stream()
            .filter(l -> l.getPeriod().equals(currentLoan.getPeriod())
                && l.getBenefPaymentAmount() != null
                && l.getRepaymentDate().compareTo(currentLoan.getRepaymentDate()) <= 0)
            .map(InstallmentLoan::getBenefPaymentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calculateDpd(InstallmentLoan loan) {
        return (int) ChronoUnit.DAYS.between(loan.getMaturityDate(), loan.getToday());
    }

    // DPD Updates
    private void setZeroDpd(InstallmentLoan loan) {
        loan.setLatestDpd(0);
        loan.setMaxDpd(0);
    }

    private void setDpdForUnpaidLoan(InstallmentLoan loan) {
        int dpd = calculateDpd(loan);
        loan.setLatestDpd(dpd);
        loan.setMaxDpd(dpd);
    }

    private void setDpdForCompletedPeriod(InstallmentLoan loan) {
        loan.setLatestDpd(0);
        int maxDpd = calculateDpd(loan);
        loan.setMaxDpd(maxDpd);
    }

    private void updateDpdForPartialPayment(InstallmentLoan loan, List<InstallmentLoan> allLoans) {
        if (!loan.getIsPartialInstallment()) {
            setDpdForUnpaidLoan(loan);
            return;
        }

        BigDecimal totalPayments = calculateTotalPaymentsUpToDate(allLoans, loan);
        if (isPeriodFullyPaid(loan, totalPayments)) {
            setDpdForCompletedPeriod(loan);
        } else {
            setDpdForUnpaidLoan(loan);
        }
    }

    // Main Method
    public List<InstallmentLoan> calculateV4(List<InstallmentLoan> installmentLoans) {
        LocalDate lastMaturityDate = findLastMaturityDate(installmentLoans);
        
        // If we're past all maturity dates, only calculate DPD for the last period
        if (installmentLoans.get(0).getToday().isAfter(lastMaturityDate)) {
            // Set all previous periods to zero DPD
            for (int i = 0; i < installmentLoans.size() - 1; i++) {
                setZeroDpd(installmentLoans.get(i));
            }
            
            // Calculate DPD only for the last period
            InstallmentLoan lastLoan = installmentLoans.get(installmentLoans.size() - 1);
            if (isGracePeriod(lastLoan)) {
                setZeroDpd(lastLoan);
            } else if (isRegularFullPayment(lastLoan)) {
                setDpdForCompletedPeriod(lastLoan);
            } else {
                int dpd = (int) ChronoUnit.DAYS.between(lastLoan.getMaturityDate(), lastLoan.getToday());
                lastLoan.setLatestDpd(dpd);
                lastLoan.setMaxDpd(dpd);
            }
            return installmentLoans;
        }
        
        // Normal case - not past all maturity dates
        int maxDpdSoFar = 0;
        for (InstallmentLoan loan : installmentLoans) {
            if (isGracePeriod(loan)) {
                setZeroDpd(loan);
                continue;
            }

            if (!isAfterMaturity(loan)) {
                setZeroDpd(loan);
                continue;
            }

            if (isRegularFullPayment(loan)) {
                setDpdForCompletedPeriod(loan);
            } else {
                updateDpdForPartialPayment(loan, installmentLoans);
            }

            // Track the highest DPD seen so far
            maxDpdSoFar = Math.max(maxDpdSoFar, loan.getMaxDpd());
            // Update current loan's max DPD to be at least as high as previous periods
            loan.setMaxDpd(Math.max(loan.getMaxDpd(), maxDpdSoFar));
        }
        
        return installmentLoans;
    }
}
