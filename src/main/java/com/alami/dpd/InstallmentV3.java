package com.alami.dpd;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class InstallmentV3 {
    private LocalDate maturityDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private RepaymentStatus repaymentStatus;
    private LocalDate repaymentDate;
    private LocalDate writtenOfDate;

    public boolean isGracePeriod() {
        return amount != null && BigDecimal.ZERO.compareTo(amount) == 0 && repaymentStatus == RepaymentStatus.GRACE_PERIOD;
    }

    public boolean isFullyPaid() {
        return repaymentStatus == RepaymentStatus.PAID;
    }

    public boolean isWrittenOff() {
        return repaymentStatus == RepaymentStatus.WRITE_OFF;
    }

    public boolean isPartiallyPaid() {
        return repaymentStatus == RepaymentStatus.PARTIAL_REPAYMENT;
    }

    public long calculateDpd(LocalDate calculationDate) {
        if (isGracePeriod() || calculationDate.compareTo(maturityDate) <= 0) {
            return 0;
        }

        if (isWrittenOff() && writtenOfDate != null) {
            if (maturityDate == null) {
                return 0;
            }
            // For written off installments, calculate DPD from write-off date to maturity date
            return java.time.temporal.ChronoUnit.DAYS.between(maturityDate, writtenOfDate);
        }

        if (isFullyPaid() && repaymentDate != null) {
            if (repaymentDate.compareTo(maturityDate) <= 0) {
                return 0;
            }
            return java.time.temporal.ChronoUnit.DAYS.between(maturityDate, repaymentDate);
        }

        return java.time.temporal.ChronoUnit.DAYS.between(maturityDate, calculationDate);
    }
}
