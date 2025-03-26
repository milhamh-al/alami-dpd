package com.alami.dpd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstallmentV3Test {

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-03) < maturity date 1st period
        PERIOD 1: maturity=2024-12-20, status=NOT_PAID
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID  
        PERIOD 3: maturity=2025-02-20, status=NOT_PAID
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=0, maxDpd=0 
        """)
    void testCase1() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-03");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(0, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-20) = maturity date 1st period
        PERIOD 1: maturity=2024-12-20, status=PAID
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID
        PERIOD 3: maturity=2025-02-20, status=NOT_PAID
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=0, maxDpd=0
        """)
    void testCase2() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-20");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(0, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-03) < maturity date 2nd period with grace periods
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PAID
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=0, maxDpd=0
        """)
    void testCase3() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-03");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(0, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-20) = maturity date 1st period with all paid
        PERIOD 1: maturity=2024-12-20, status=PAID
        PERIOD 2: maturity=2025-01-20, status=PAID
        PERIOD 3: maturity=2025-02-20, status=PAID
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=0, maxDpd=0
        """)
    void testCase4() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-20");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(0, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-25) > maturity date 1st period with grace period
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=0, maxDpd=0
        """)
    void testCase5() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-25");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(0, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-01-21) > maturity date 2nd period
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=1, maxDpd=1
        """)
    void testCase6() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2025-01-21");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(1, dpd.getLatestDpd());
        assertEquals(1, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-01-25) > maturity date 2nd period
        With partial repayment at 2nd period
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PARTIAL_REPAYMENT, paidAmount=25M
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=5, maxDpd=5
        """)
    void testCase7() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2025-01-25");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(5, dpd.getLatestDpd());
        assertEquals(5, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-04-30) > maturity date 5th period
        With partial repayment at 2nd period
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PARTIAL_REPAYMENT, paidAmount=25M
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=100, maxDpd=100
        """)
    void testCase8() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(100, dpd.getLatestDpd());
        assertEquals(100, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-04-30) > maturity date 5th period
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PAID, repaymentDate=2025-01-25
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=41, maxDpd=41
        """)
    void testCase9() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(41, dpd.getLatestDpd());
        assertEquals(41, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-04-30) > maturity date 5th period
        With late payments
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PAID, repaymentDate=2025-01-25
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=PAID, repaymentDate=2025-04-20
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=10, maxDpd=31
        """)
    void testCase10() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-04-20"))
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(Arrays.asList(
                        installment1, installment2, installment3, installment4, installment5)
                )
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-04-30");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(10, dpd.getLatestDpd());
        assertEquals(31, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-04-30) > maturity date 5th period
        With late payments and REPAYMENT_SUCCESS status
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PAID, repaymentDate=2025-01-25
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=PAID, repaymentDate=2025-04-30
        PERIOD 5: maturity=2025-04-20, status=PAID, repaymentDate=2025-05-05
        
        Expected: latestDpd=15, maxDpd=41
        """)
    void testCase11() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-04-30"))
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-05-05"))
                .period(5)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(Arrays.asList(
                        installment1, installment2, installment3, installment4, installment5)
                )
                .status(Status.REPAYMENT_SUCCESS)
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-04-30");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(15, dpd.getLatestDpd());
        assertEquals(41, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-08-01) > maturity date 6th period
        With WRITE_OFF status
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PAID, repaymentDate=2025-01-25
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=PAID, repaymentDate=2025-04-30
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        PERIOD 6: status=WRITE_OFF, writtenOfDate=2025-08-01
        
        Expected: latestDpd=103, maxDpd=103
        """)
    void testCase12() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-01-25"))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .paidAmount(null)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-04-30"))
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
                .build();

        InstallmentV3 installment6 = InstallmentV3.builder()
                .repaymentStatus(RepaymentStatus.WRITE_OFF)
                .writtenOfDate(LocalDate.parse("2025-08-01"))
                .period(6)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(Arrays.asList(
                        installment1, installment2, installment3, 
                        installment4, installment5, installment6)
                )
                .status(Status.WRITE_OFF)
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-08-01");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(103, dpd.getLatestDpd());
        assertEquals(103, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2024-12-21) > maturity date 1st period by 1 day
        PERIOD 1: maturity=2024-12-20, status=NOT_PAID
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID
        PERIOD 3: maturity=2025-02-20, status=NOT_PAID
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=1, maxDpd=1
        """)
    void testCase13() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2024-12-21");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(1, dpd.getLatestDpd());
        assertEquals(1, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-01-25) > maturity date 1st period by 36 days
        PERIOD 1: maturity=2024-12-20, status=NOT_PAID
        PERIOD 2: maturity=2025-01-20, status=NOT_PAID
        PERIOD 3: maturity=2025-02-20, status=NOT_PAID
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=36, maxDpd=36
        """)
    void testCase14() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
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

        LocalDate calculationDate = LocalDate.parse("2025-01-25");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(36, dpd.getLatestDpd());
        assertEquals(36, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-04-24) > maturity date 5th period
        With multiple repayment statuses
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PARTIAL_REPAYMENT, paidAmount=25M
        PERIOD 2: maturity=2025-01-20, status=PAID, paidAmount=25M
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=35, maxDpd=35
        """)
    void testCase15() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
                .repaymentDate(LocalDate.parse("2025-01-24"))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .period(2)
                .build();

        InstallmentV3 installment21 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-02-19"))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(
                        Arrays.asList(
                                installment1, 
                                installment2,
                                installment21,
                                installment3,
                                installment4,
                                installment5
                                ))
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-04-24");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(35, dpd.getLatestDpd());
        assertEquals(35, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Calculate DPD when calculation date (2025-03-24) > maturity date 4th period
        With multiple repayment statuses
        PERIOD 1: maturity=2024-12-20, status=GRACE_PERIOD, amount=0
        PERIOD 2: maturity=2025-01-20, status=PARTIAL_REPAYMENT, paidAmount=25M
        PERIOD 2: maturity=2025-01-20, status=PAID, paidAmount=25M
        PERIOD 3: maturity=2025-02-20, status=GRACE_PERIOD, amount=0
        PERIOD 4: maturity=2025-03-20, status=NOT_PAID
        PERIOD 5: maturity=2025-04-20, status=NOT_PAID
        
        Expected: latestDpd=4, maxDpd=30
        """)
    void testCase16() {
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(1)
                .build();

        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
                .repaymentDate(LocalDate.parse("2025-01-24"))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .period(2)
                .build();

        InstallmentV3 installment21 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2025-02-19"))
                .paidAmount(BigDecimal.valueOf(25_000_000))
                .period(2)
                .build();

        InstallmentV3 installment3 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-02-20"))
                .amount(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
                .period(3)
                .build();

        InstallmentV3 installment4 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-03-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(4)
                .build();

        InstallmentV3 installment5 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-04-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.NOT_PAID)
                .period(5)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(
                        Arrays.asList(
                                installment1, 
                                installment2,
                                installment21,
                                installment3,
                                installment4,
                                installment5
                                ))
                .build();

        LocalDate calculationDate = LocalDate.parse("2025-03-24");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);
        assertEquals(4, dpd.getLatestDpd());
        assertEquals(30, dpd.getMaxDpd());
    }

    @Test
    @DisplayName("""
        todo , don't understand the logic
        First installment - Due Dec 20, paid on time
        Second installment - Due Jan 20, written off Feb 10
        Expected: latestDpd=21, maxDpd=21
        """)
    void testCase17() {
        // First installment - Due Dec 20, paid on time
        InstallmentV3 installment1 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2024-12-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .paidAmount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.PAID)
                .repaymentDate(LocalDate.parse("2024-12-20"))
                .period(1)
                .build();

        // Second installment - Due Jan 20, written off Feb 10
        InstallmentV3 installment2 = InstallmentV3.builder()
                .maturityDate(LocalDate.parse("2025-01-20"))
                .amount(BigDecimal.valueOf(50_000_000))
                .repaymentStatus(RepaymentStatus.WRITE_OFF)
                .writtenOfDate(LocalDate.parse("2025-02-10"))
                .period(2)
                .build();

        InstallmentLoanV3 loan = InstallmentLoanV3.builder()
                .installments(Arrays.asList(installment1, installment2))
                .status(Status.WRITE_OFF)
                .build();

        // Calculate on Feb 10 (write off date)
        LocalDate calculationDate = LocalDate.parse("2025-02-10");
        Dpd dpd = loan.calculateLatestDpd(calculationDate);

        // DPD should be 21 days (Jan 20 to Feb 10)
        // We use latest maturity of Jan 20 (not Feb 20 since it's not due yet)
        assertEquals(21, dpd.getLatestDpd());
        assertEquals(21, dpd.getMaxDpd());
    }
}
