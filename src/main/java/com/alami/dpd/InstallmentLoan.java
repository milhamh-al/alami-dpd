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
public class InstallmentLoan {
    LocalDate maturityDate;
    Integer period;
    BigDecimal amount;
    BigDecimal benefPaymentAmount;
    LocalDate repaymentDate;
    LocalDate today;
    Status status;
    Integer latestDpd;
    Integer maxDpd;
    Boolean isPartialInstallment;
}
