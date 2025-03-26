package com.alami.dpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class InstallmentTest {
    private static Installment installment;

    @BeforeAll
    static void init() {
        installment = new Installment();
    }
    @Nested
    class BeforeMaturityDate {
        @Test
        @DisplayName("""
            case 1
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - not paid 🚫
        """)
        void first_maturity_not_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 2
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - paid ✅
        """)
        void first_maturity_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 3
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - partial installment 🧩
        """)
        void first_maturity_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 4
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - first partial installment 🧩
            - second partial installment paid ✅
        """)
        void first_maturity_second_partial_installment_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();

            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 18))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 18))
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 5
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - grace period ⏱️
        """)
        void first_maturity_grace_period() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 6
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity paid ✅
            - second maturity not paid 🚫
        """)
        void second_maturity_not_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();

            installmentLoans.add(installmentLoan1);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 7
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity paid ✅
            - second maturity paid ✅
        """)
        void second_maturity_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 8
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity paid ✅
            - second maturity grace period ⏱️
        """)
        void second_maturity_grace_period() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 9
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity paid ✅
            - second maturity first partial installment 🧩
        """)
        void second_maturity_first_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 10
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity paid ✅
            - second maturity first partial installment 🧩
            - second maturity second partial installment paid ✅
        """)
        void second_maturity_second_partial_installment_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .isPartialInstallment(true)
                                            .build();
            InstallmentLoan installmentLoan3 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 15))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 15))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);
            installmentLoans.add(installmentLoan3);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());

            assertEquals(0, res.get(2).getLatestDpd());
            assertEquals(0, res.get(2).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 11
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - first maturity grace period ⏱️
            - second maturity grace period ⏱️
        """)
        void continuous_grace_period() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 12
            - first maturity grace period ⏱️
            - second maturity not paid 🚫
        """)
        void grace_period_1_not_paid_period_2() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 13
            - first maturity grace period ⏱️
            - second maturity paid ✅
            - third maturity grace period ⏱️
        """)
        void grace_period_1_3_paid_period_3() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 3))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 10))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan3 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 2, 18))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);
            installmentLoans.add(installmentLoan3);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());

            assertEquals(0, res.get(2).getLatestDpd());
            assertEquals(0, res.get(2).getMaxDpd());
        }
    }

    @Nested
    class AfterMaturityDate {
        @Test
        @DisplayName("""
            case 1
            - today > first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-24
            - not paid 🚫
        """)
        void first_maturity_not_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(4, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 2
            - today > first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-24
            - paid ✅
        """)
        void first_maturity_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 3
            - today > first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-24
            - first period partial installment 🧩
        """)
        void first_maturity_first_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(4, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 4
            - today > first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-24
            - first period first partial installment 🧩
            - first period second partial installment paid ✅
        """)
        void first_maturity_second_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(true)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 27 ))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2024, 12, 27))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(4, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(7, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 5
            - today > first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-24
            - first period grace period ⏱️
        """)
        void first_maturity_grace_period() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(0, res.get(0).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 6
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - not paid 🚫
        """)
        void second_maturity_not_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .today(LocalDate.of(2025, 1, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(4, res.get(1).getLatestDpd());
            assertEquals(4, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 7
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - paid ✅
        """)
        void second_maturity_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2025, 1, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(4, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 8
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - paid ✅
            - 1st period max dpd > 2nd period max dpd
        """)
        void second_maturity_paid_use_first_period_max_dpd() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 31))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 31))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2025, 1, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(11, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(11, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 9
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - grace period ⏱️
        """)
        void second_maturity_grace_period() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.ZERO)
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 24))
                                            .isPartialInstallment(false)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(0, res.get(1).getLatestDpd());
            assertEquals(0, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 10
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - second maturity first partial installment 🧩
        """)
        void second_maturity_first_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 28))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 28))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(8, res.get(1).getLatestDpd());
            assertEquals(8, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 11
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - second maturity first partial installment 🧩
        """)
        void second_maturity_second_partial_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 24))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 28))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 28))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(4, res.get(0).getMaxDpd());

            assertEquals(8, res.get(1).getLatestDpd());
            assertEquals(8, res.get(1).getMaxDpd());
        }

        @Test
        @DisplayName("""
            case 12
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-24
            - second maturity paid, installment paid off✅
        """)
        void second_maturity_installment_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 31))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .today(LocalDate.of(2024, 12, 31))
                                            .isPartialInstallment(false)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 25))
                                            .status(Status.READY_FOR_PARTIAL_REPAYMENT)
                                            .today(LocalDate.of(2025, 1, 25))
                                            .isPartialInstallment(true)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
            assertEquals(0, res.get(0).getLatestDpd());
            assertEquals(11, res.get(0).getMaxDpd());

            assertEquals(5, res.get(1).getLatestDpd());
            assertEquals(11, res.get(1).getMaxDpd());
        }
    
        @Nested
        class PastAllMaturityDates {
            @Test
            @DisplayName("""
                Past All Maturity Dates
                - today = 2025-04-30
                - period 1 (2024-12-20): Grace Period
                - period 2 (2025-01-20): Paid on 2025-01-25
                - period 3 (2025-02-20): Grace Period
                - period 4 (2025-03-20): Not Paid
                - period 5 (2025-04-20): Not Paid
                """)
            void past_all_maturity_dates() {
                List<InstallmentLoan> installmentLoans = new ArrayList<>();
    
                // Period 1 - Grace Period
                InstallmentLoan loan1 = InstallmentLoan.builder()
                    .period(1)
                    .maturityDate(LocalDate.of(2024, 12, 20))
                    .amount(BigDecimal.ZERO)
                    .benefPaymentAmount(null)
                    .repaymentDate(null)
                    .status(Status.DISBURSEMENT)
                    .today(LocalDate.of(2025, 4, 30))
                    .isPartialInstallment(false)
                    .build();
    
                // Period 2 - Paid
                InstallmentLoan loan2 = InstallmentLoan.builder()
                    .period(2)
                    .maturityDate(LocalDate.of(2025, 1, 20))
                    .amount(BigDecimal.valueOf(50_000_000))
                    .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                    .repaymentDate(LocalDate.of(2025, 1, 25))
                    .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                    .today(LocalDate.of(2025, 4, 30))
                    .isPartialInstallment(false)
                    .build();
    
                // Period 3 - Grace Period
                InstallmentLoan loan3 = InstallmentLoan.builder()
                    .period(3)
                    .maturityDate(LocalDate.of(2025, 2, 20))
                    .amount(BigDecimal.ZERO)
                    .benefPaymentAmount(null)
                    .repaymentDate(null)
                    .status(Status.DISBURSEMENT)
                    .today(LocalDate.of(2025, 4, 30))
                    .isPartialInstallment(false)
                    .build();
    
                // Period 4 - Not Paid
                InstallmentLoan loan4 = InstallmentLoan.builder()
                    .period(4)
                    .maturityDate(LocalDate.of(2025, 3, 20))
                    .amount(BigDecimal.valueOf(50_000_000))
                    .benefPaymentAmount(null)
                    .repaymentDate(null)
                    .status(Status.DISBURSEMENT)
                    .today(LocalDate.of(2025, 4, 30))
                    .isPartialInstallment(false)
                    .build();
    
                // Period 5 - Not Paid
                InstallmentLoan loan5 = InstallmentLoan.builder()
                    .period(5)
                    .maturityDate(LocalDate.of(2025, 4, 20))
                    .amount(BigDecimal.valueOf(50_000_000))
                    .benefPaymentAmount(null)
                    .repaymentDate(null)
                    .status(Status.DISBURSEMENT)
                    .today(LocalDate.of(2025, 4, 30))
                    .isPartialInstallment(false)
                    .build();
    
                installmentLoans.add(loan1);
                installmentLoans.add(loan2);
                installmentLoans.add(loan3);
                installmentLoans.add(loan4);
                installmentLoans.add(loan5);
    
                List<InstallmentLoan> res = installment.calculateV4(installmentLoans);
    
                // When past all maturity dates, only calculate DPD from last period
                // All previous periods should have DPD = 0 since they don't matter anymore
                assertEquals(0, res.get(0).getLatestDpd());
                assertEquals(0, res.get(0).getMaxDpd());
    
                assertEquals(0, res.get(1).getLatestDpd());
                assertEquals(0, res.get(1).getMaxDpd());
    
                assertEquals(0, res.get(2).getLatestDpd());
                assertEquals(0, res.get(2).getMaxDpd());
    
                assertEquals(0, res.get(3).getLatestDpd());
                assertEquals(0, res.get(3).getMaxDpd());
    
                // Only last period has DPD (10 days from 2025-04-20 to 2025-04-30)
                assertEquals(10, res.get(4).getLatestDpd());
                assertEquals(10, res.get(4).getMaxDpd());
            }
        }
    }
}
