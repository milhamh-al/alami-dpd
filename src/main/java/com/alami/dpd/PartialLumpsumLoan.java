package com.alami.dpd;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PartialLumpsumLoan {
    Integer period;
    private BigDecimal amount;
    private BigDecimal benefPaymentAmount;
    LocalDate maturityDate;
    LocalDate repaymentDate;
    LocalDate today;
    RepaymentStatus status;
}
