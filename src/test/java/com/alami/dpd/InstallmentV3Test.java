package com.alami.dpd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstallmentV3Test {

    @Test
    void testCase1() {
        // Case 9: Testing grace period handling
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
    void testCase9() {
        // Case 9: Testing grace period handling
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
        assertEquals(0, dpd.getLatestDpd());
        assertEquals(41, dpd.getMaxDpd());
    }

    @Test
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
}
