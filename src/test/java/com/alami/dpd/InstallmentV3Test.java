package com.alami.dpd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstallmentV3Test {

    @Test
    void testCase9() {
        // Case 9: Testing grace period handling
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(
                        Arrays.asList(
                                installment1, 
                                installment2,
                                installment3,
                                installment4,
                                installment5
                                ))
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-04-30");
        assertEquals(41, loan.calculateLatestDpd(calculationDate));
    }

    @Test
    void testCase10() {
        // Case 10: Testing historical DPD calculation
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.ONE)
                .paidAmount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-25"))
                .amount(BigDecimal.ONE)
                .paidAmount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(Arrays.asList(installment1, installment2))
                .build();

        // Historical dates for DPD calculation
        List<LocalDate> historicalDates = Arrays.asList(
                LocalDate.parse("2025-01-22"),  // DPD for inst1 = 2, inst2 = 0
                LocalDate.parse("2025-01-27"),  // DPD for inst1 = 7, inst2 = 2
                LocalDate.parse("2025-01-30")   // DPD for inst1 = 10, inst2 = 5
        );

        assertEquals(10, loan.calculateMaxHistoricalDpd(historicalDates));
    }
}
