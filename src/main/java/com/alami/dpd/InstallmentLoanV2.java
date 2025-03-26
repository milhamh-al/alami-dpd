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
public class InstallmentLoanV2 {
    // Schedule Information
    private LocalDate maturityDate;
    private Integer period;
    private BigDecimal amount;
    
    // Payment Information
    private BigDecimal benefPaymentAmount;
    private LocalDate repaymentDate;
    private RepaymentStatus repaymentStatus;
    
    // Current Date
    private LocalDate today;
    
    // DPD Information
    private Integer latestDpd;
    private Integer maxDpd;
}