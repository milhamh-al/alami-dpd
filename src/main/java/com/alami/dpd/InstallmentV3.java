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

    public boolean isGracePeriod() {
        return BigDecimal.ZERO.compareTo(amount) == 0 || repaymentStatus == RepaymentStatus.GRACE_PERIOD;
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
        if (isGracePeriod() || isFullyPaid() || isWrittenOff() || calculationDate.compareTo(maturityDate) <= 0) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(maturityDate, calculationDate);
    }
}
