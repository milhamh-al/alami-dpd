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

    @Test
    @DisplayName("""
        Edge Case 1: Null or Empty Installment List
        Scenarios:
        1. Null list -> Should return DPD(0,0)
        2. Empty list -> Should return DPD(0,0)
        Validates defensive programming for invalid inputs
        """)
    void null_or_empty_list() {
        // Test null list
        Dpd dpdNull = installment.calculateV4(null, LocalDate.now());
        assertEquals(0, dpdNull.getDpdTerakhir());
        assertEquals(0, dpdNull.getDpdMax());

        // Test empty list
        Dpd dpdEmpty = installment.calculateV4(new ArrayList<>(), LocalDate.now());
        assertEquals(0, dpdEmpty.getDpdTerakhir());
        assertEquals(0, dpdEmpty.getDpdMax());
    }

    @Nested
    class BeforeMaturityDate {
        @Test
        @DisplayName("""
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - not paid off 🚫
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
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 3);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today < first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-3
            - paid off ✅
        """)
        void first_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 3);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - not paid off 🚫
        """)
        void second_maturity_not_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 10);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today < second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-10
            - paid off ✅
        """)
        void second_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 10);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }
    }

    @Nested
    class EqualMaturityDate {
        @Test
        @DisplayName("""
            - today = first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-20
            - not paid off 🚫
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
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 20);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today = first installment period maturity date
            - first installment period maturity date = 2024-12-20
            - today = 2024-12-20
            - paid off ✅
        """)
        void first_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 20))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 20);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today = sceond installment period maturity date
            - second installment period maturity date = 2024-12-20
            - today = 2025-1-10
            - not paid off 🚫    
        """)
        void second_maturity_not_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 20))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 20);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today = second installment period maturity date
            - second installment period maturity date = 2024-12-20
            - today = 2025-1-10
            - paid off ✅    
        """)
        void second_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 20))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 20))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 20);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }
    }

    @Nested
    class AfterMaturityDate {
        @Test
        @DisplayName("""
            - today > first installment period maturity date
            - first installment period maturity date = 2025-1-20
            - today = 2024-12-24
            - not paid off 🚫
        """)
        void first_maturity_not_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2025, 1, 24);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(4, dpd.getDpdTerakhir());
            assertEquals(4, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today > first installment period maturity date
            - first installment period maturity date = 2025-1-20
            - today = 2024-12-24
            - paid off ✅
        """)
        void first_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 24))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 24);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-25
            - not paid off 🚫
        """)
        void second_maturity_not_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 24))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(5, dpd.getDpdTerakhir());
            assertEquals(5, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            - today > second installment period maturity date
            - second installment period maturity date = 2025-1-20
            - today = 2025-1-25
            - paid off ✅
        """)
        void second_maturity_paid_off() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 3))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 10))
                                            .status(Status.PARTIAL_REPAYMENT_SUCCESS)
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }
    }

    @Nested
    class CalculateV4Tests {
        @Test
        @DisplayName("V4 - case 1: all installments paid")
        void all_installments_paid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            // First installment with payment
            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2024, 12, 20))
                                            .status(Status.DISBURSEMENT) // Status doesn't matter
                                            .build();

            // Second installment with payment
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
                                            .repaymentDate(LocalDate.of(2025, 1, 15))
                                            .status(Status.DISBURSEMENT) // Status doesn't matter
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("V4 - case 2: single unpaid installment")
        void single_unpaid_installment() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(5, dpd.getDpdTerakhir()); // 5 days late
            assertEquals(5, dpd.getDpdMax());
        }

        @Test
        @DisplayName("""
            V4 - Case 3: Multiple Unpaid Installments
            Scenario:
            - First installment: 35 days late (Dec 20 -> Jan 25)
            - Second installment: 5 days late (Jan 20 -> Jan 25)
            Expected:
            - DPD should be based on earliest unpaid (35 days)
            """)
        void multiple_unpaid_installments() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            // First installment no payment - 35 days late
            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null) // No payment
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_REPAYMENT) // Status doesn't matter
                                            .build();

            // Second installment no payment - 5 days late
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null) // No payment
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_REPAYMENT) // Status doesn't matter
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(36, dpd.getDpdTerakhir()); // Based on earliest unpaid (Dec 20)
            assertEquals(36, dpd.getDpdMax());
        }

        @Test
        @DisplayName("V4 - case 4: before first maturity")
        void before_first_maturity() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            InstallmentLoan installmentLoan = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null)
                                            .repaymentDate(null)
                                            .status(Status.DISBURSEMENT)
                                            .build();

            installmentLoans.add(installmentLoan);

            LocalDate today = LocalDate.of(2024, 12, 3);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(0, dpd.getDpdTerakhir());
            assertEquals(0, dpd.getDpdMax());
        }

        @Test
        @DisplayName("V4 - case 5: mix of paid and unpaid")
        void mix_paid_unpaid() {
            List<InstallmentLoan> installmentLoans = new ArrayList<>();

            // First installment with payment
            InstallmentLoan installmentLoan1 = InstallmentLoan
                                            .builder()
                                            .period(1)
                                            .maturityDate(LocalDate.of(2024, 12, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(BigDecimal.valueOf(50_000_000)) // Has payment
                                            .repaymentDate(LocalDate.of(2024, 12, 20))
                                            .status(Status.READY_FOR_SETTLEMENT) // Status doesn't matter
                                            .build();

            // Second installment no payment - 5 days late
            InstallmentLoan installmentLoan2 = InstallmentLoan
                                            .builder()
                                            .period(2)
                                            .maturityDate(LocalDate.of(2025, 1, 20))
                                            .amount(BigDecimal.valueOf(50_000_000))
                                            .benefPaymentAmount(null) // No payment
                                            .repaymentDate(null)
                                            .status(Status.READY_FOR_REPAYMENT) // Status doesn't matter
                                            .build();

            installmentLoans.add(installmentLoan1);
            installmentLoans.add(installmentLoan2);

            LocalDate today = LocalDate.of(2025, 1, 25);

            Dpd dpd = installment.calculateV4(installmentLoans, today);

            assertEquals(5, dpd.getDpdTerakhir()); // Based on unpaid installment
            assertEquals(5, dpd.getDpdMax());
        }
    }
}
