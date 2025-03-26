package com.alami.dpd;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class InstallmentLoan {
    // Schedule Information
    private LocalDate maturityDate;
    private Integer period;
    private BigDecimal amount;
    
    // Payment Information
    private BigDecimal benefPaymentAmount;
    private LocalDate repaymentDate;
    private Status status;
    private Boolean isPartialInstallment;
    
    // DPD Calculation
    private LocalDate today;
    private Integer latestDpd;
    private Integer maxDpd;
    
    // Helper methods
    public boolean hasAction() {
        return isGracePeriod() || hasPayment();
    }
    
    public boolean isGracePeriod() {
        return amount != null && amount.equals(BigDecimal.ZERO);
    }
    
    public boolean hasPayment() {
        return benefPaymentAmount != null && repaymentDate != null;
    }
    
    public boolean isPastDue() {
        return today != null && maturityDate != null && today.isAfter(maturityDate);
    }
    
    public boolean isFullyPaid() {
        return benefPaymentAmount != null && amount != null && 
               benefPaymentAmount.compareTo(amount) >= 0;
    }
}
