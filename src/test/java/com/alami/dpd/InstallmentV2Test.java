package com.alami.dpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InstallmentV2Test {
    private static InstallmentV2 installment;

    @BeforeAll
    static void init() {
        installment = new InstallmentV2();
    }

    private InstallmentLoanV2 createLoan(
        int period,
        LocalDate maturityDate,
        BigDecimal amount,
        BigDecimal benefPaymentAmount,
        LocalDate repaymentDate,
        RepaymentStatus status,
        LocalDate today
    ) {
        return InstallmentLoanV2.builder()
            .period(period)
            .maturityDate(maturityDate)
            .amount(amount)
            .benefPaymentAmount(benefPaymentAmount)
            .repaymentDate(repaymentDate)
            .repaymentStatus(status)
            .today(today)
            .build();
    }

    @Test
    @DisplayName("""
        Case 1 - All Periods Not Paid
        Today: 03-12-2024
        Schedule:
        1. 20-12-2024: Not Paid
        2. 20-01-2025: Not Paid
        3. 20-02-2025: Not Paid
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0 (today is before first maturity)
        """)
    void case_1_all_not_paid() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2024, 12, 3);

        for (int i = 0; i < 5; i++) {
            loans.add(createLoan(
                i + 1,
                LocalDate.of(2024, 12, 20).plusMonths(i),
                BigDecimal.valueOf(50_000_000),
                null,
                null,
                RepaymentStatus.NOT_PAID,
                today
            ));
        }

        Dpd result = installment.calculate(loans);
        assertEquals(0, result.getLatestDpd());
        assertEquals(0, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 2 - All Periods Not Paid (Similar to Case 1)
        Today: 20-12-2024
        Schedule:
        1. 20-12-2024: Not Paid
        2. 20-01-2025: Not Paid
        3. 20-02-2025: Not Paid
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0 (today is before first maturity)
        """)
    void case_2_all_not_paid() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2024, 12, 20);

        for (int i = 0; i < 5; i++) {
            loans.add(createLoan(
                i + 1,
                LocalDate.of(2024, 12, 20).plusMonths(i),
                BigDecimal.valueOf(50_000_000),
                null,
                null,
                RepaymentStatus.NOT_PAID,
                today
            ));
        }

        Dpd result = installment.calculate(loans);
        assertEquals(0, result.getLatestDpd());
        assertEquals(0, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 3 - First Period Grace Period
        Today: 03-12-2024
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Not Paid
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0
        """)
    void case_3_first_grace_period() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2024, 12, 3);

        // First period - Grace Period
        loans.add(createLoan(
            1,
            LocalDate.of(2024, 12, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.GRACE_PERIOD,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 1, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 2, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.GRACE_PERIOD,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 3, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 4, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));


        Dpd result = installment.calculate(loans);
        assertEquals(0, result.getLatestDpd());
        assertEquals(0, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 4 - First Period Grace Period (Similar to Case 3)
        Today: 03-12-2024
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Not Paid
        3. 20-02-2025: Not Paid
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0
        """)
    void case_4_first_grace_period() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2024, 12, 20);

        // First period - Grace Period
        loans.add(createLoan(
            1,
            LocalDate.of(2024, 12, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.GRACE_PERIOD,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 1, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 2, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.GRACE_PERIOD,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 3, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));

        loans.add(createLoan(
            1,
            LocalDate.of(2025, 4, 20),
            BigDecimal.ZERO,
            null,
            null,
            RepaymentStatus.NOT_PAID,
            today
        ));

        Dpd result = installment.calculate(loans);
        assertEquals(0, result.getLatestDpd());
        assertEquals(0, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 5 - Grace Period, Paid, Not Paid ?????
        Today: 25-12-2024
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Paid
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0 (today is before first non-grace, non-paid maturity)
        """)
    void case_5_grace_paid_not_paid() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2024, 12, 25);

        loans.add(
            InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(0, result.getLatestDpd());
        assertEquals(0, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 6 - Grace Period, Paid, Not Paid (Similar to Case 5)
        Today: 25-12-2024
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Paid
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 0 (today is before first non-grace, non-paid maturity)
        """)
    void case_6_grace_paid_not_paid() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 1, 21);

        loans.add(
            InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(1, result.getLatestDpd());
        assertEquals(1, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 7 - Grace Period and Partial Payment
        Today: 25-01-2025
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Partial Installment
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 5 
        """)
    void case_7_grace_and_partial() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 1, 25);

        loans.add(
            InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
            .repaymentDate(LocalDate.of(2025, 1, 25))
            .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(5, result.getLatestDpd());
        assertEquals(5, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 8 - Grace Period and Partial Payment (Similar to Case 7)
        Today: 30-4-2025
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Partial Repayment
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 100
        """)
    void case_8_grace_and_partial() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 4, 30);

        loans.add(
            InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(BigDecimal.valueOf(25_000_000))
            .repaymentDate(LocalDate.of(2025, 1, 25))
            .repaymentStatus(RepaymentStatus.PARTIAL_REPAYMENT)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        loans.add(
            InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(100, result.getLatestDpd());
        assertEquals(100, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 9 - Past All Maturity Dates
        Today: 30-04-2025
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Paid on 25-01-2025
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Not Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 41 (from earliest unpaid period 20-03-2025)
        """)
    void case_9_past_all_maturity_dates() {
        List<InstallmentLoanV2> loans = new ArrayList<>();
        LocalDate today = LocalDate.of(2025, 4, 30);

        // Period 1 - Grace Period
        loans.add(InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        // Period 2 - Paid
        loans.add(InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
            .repaymentDate(LocalDate.of(2025, 1, 25))
            .repaymentStatus(RepaymentStatus.PAID)
            .today(today)
            .build());

        // Period 3 - Grace Period
        loans.add(InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(today)
            .build());

        // Period 4 - Not Paid
        loans.add(InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        // Period 5 - Not Paid
        loans.add(InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(today)
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(41, result.getLatestDpd());
        assertEquals(41, result.getMaxDpd());
    }

    @Test
    @DisplayName("""
        Case 10 - Multiple Payment Statuses
        Today: 30-04-2025
        Schedule:
        1. 20-12-2024: Grace Period
        2. 20-01-2025: Paid
        3. 20-02-2025: Grace Period
        4. 20-03-2025: Paid
        5. 20-04-2025: Not Paid
        Expected: DPD = 10
        """)
    void case_10_multiple_statuses() {
        List<InstallmentLoanV2> loans = new ArrayList<>();

        // Period 1 - Grace Period
        loans.add(InstallmentLoanV2.builder()
            .period(1)
            .maturityDate(LocalDate.of(2024, 12, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(LocalDate.of(2024, 12, 24))
            .build());

        // Period 2 - Paid
        loans.add(InstallmentLoanV2.builder()
            .period(2)
            .maturityDate(LocalDate.of(2025, 1, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
            .repaymentDate(LocalDate.of(2025, 1, 25))
            .repaymentStatus(RepaymentStatus.PAID)
            .today(LocalDate.of(2025, 1, 25))
            .build());

        // Period 3 - Grace Period
        loans.add(InstallmentLoanV2.builder()
            .period(3)
            .maturityDate(LocalDate.of(2025, 2, 20))
            .amount(BigDecimal.ZERO)
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.GRACE_PERIOD)
            .today(LocalDate.of(2025, 2, 27))
            .build());

        // Period 4 - Paid
        loans.add(InstallmentLoanV2.builder()
            .period(4)
            .maturityDate(LocalDate.of(2025, 3, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(BigDecimal.valueOf(50_000_000))
            .repaymentDate(LocalDate.of(2025, 4, 20))
            .repaymentStatus(RepaymentStatus.PAID)
            .today(LocalDate.of(2025, 4, 20))
            .build());

        // Period 5 - Not Paid
        loans.add(InstallmentLoanV2.builder()
            .period(5)
            .maturityDate(LocalDate.of(2025, 4, 20))
            .amount(BigDecimal.valueOf(50_000_000))
            .benefPaymentAmount(null)
            .repaymentDate(null)
            .repaymentStatus(RepaymentStatus.NOT_PAID)
            .today(LocalDate.of(2025, 4, 30))
            .build());

        Dpd result = installment.calculate(loans);
        assertEquals(10, result.getLatestDpd());
        assertEquals(31, result.getMaxDpd());
    }
    //     2. 20-01-2025: Paid
    //     3. 20-02-2025: Grace Period
    //     4. 20-03-2025: Not Paid
    //     5. 20-04-2025: Not Paid
    //     Expected: DPD = 41 (from earliest unpaid period 20-03-2025)
    //     """)
    // void case_11_multiple_statuses() {
    //     List<InstallmentLoanV2> loans = new ArrayList<>();
    //     LocalDate today = LocalDate.of(2025, 4, 30);

    //     // Period 1 - Grace Period
    //     loans.add(createLoan(
    //         1,
    //         LocalDate.of(2024, 12, 20),
    //         BigDecimal.ZERO,
    //         null,
    //         null,
    //         Status.DISBURSEMENT,
    //         today
    //     ));

    //     // Period 2 - Paid
    //     loans.add(createLoan(
    //         2,
    //         LocalDate.of(2025, 1, 20),
    //         BigDecimal.valueOf(50_000_000),
    //         BigDecimal.valueOf(50_000_000),
    //         LocalDate.of(2025, 1, 25),
    //         Status.PARTIAL_REPAYMENT_SUCCESS,
    //         today
    //     ));

    //     // Period 3 - Grace Period
    //     loans.add(createLoan(
    //         3,
    //         LocalDate.of(2025, 2, 20),
    //         BigDecimal.ZERO,
    //         null,
    //         null,
    //         Status.DISBURSEMENT,
    //         today
    //     ));

    //     // Periods 4-5 Not Paid
    //     for (int i = 3; i < 5; i++) {
    //         loans.add(createLoan(
    //             i + 1,
    //             LocalDate.of(2025, i + 1, 20),
    //             BigDecimal.valueOf(50_000_000),
    //             null,
    //             null,
    //             Status.DISBURSEMENT,
    //             today
    //         ));
    //     }

    //     Dpd result = installment.calculate(loans);
    //     assertEquals(41, result.getLatestDpd());
    //     assertEquals(41, result.getMaxDpd());
    // }

    // @Test
    // @DisplayName("""
    //     Case 12 - Multiple Payment Statuses (Similar to Case 10 and 11)
    //     Today: 30-04-2025
    //     Schedule:
    //     1. 20-12-2024: Grace Period
    //     2. 20-01-2025: Paid
    //     3. 20-02-2025: Grace Period
    //     4. 20-03-2025: Not Paid
    //     5. 20-04-2025: Not Paid
    //     Expected: DPD = 41 (from earliest unpaid period 20-03-2025)
    //     """)
    // void case_12_multiple_statuses() {
    //     List<InstallmentLoanV2> loans = new ArrayList<>();
    //     LocalDate today = LocalDate.of(2025, 4, 30);

    //     // Period 1 - Grace Period
    //     loans.add(createLoan(
    //         1,
    //         LocalDate.of(2024, 12, 20),
    //         BigDecimal.ZERO,
    //         null,
    //         null,
    //         Status.DISBURSEMENT,
    //         today
    //     ));

    //     // Period 2 - Paid
    //     loans.add(createLoan(
    //         2,
    //         LocalDate.of(2025, 1, 20),
    //         BigDecimal.valueOf(50_000_000),
    //         BigDecimal.valueOf(50_000_000),
    //         LocalDate.of(2025, 1, 25),
    //         Status.PARTIAL_REPAYMENT_SUCCESS,
    //         today
    //     ));

    //     // Period 3 - Grace Period
    //     loans.add(createLoan(
    //         3,
    //         LocalDate.of(2025, 2, 20),
    //         BigDecimal.ZERO,
    //         null,
    //         null,
    //         Status.DISBURSEMENT,
    //         today
    //     ));

    //     // Periods 4-5 Not Paid
    //     for (int i = 3; i < 5; i++) {
    //         loans.add(createLoan(
    //             i + 1,
    //             LocalDate.of(2025, i + 1, 20),
    //             BigDecimal.valueOf(50_000_000),
    //             null,
    //             null,
    //             Status.DISBURSEMENT,
    //             today
    //         ));
    //     }

    //     Dpd result = installment.calculate(loans);
    //     assertEquals(41, result.getLatestDpd());
    //     assertEquals(41, result.getMaxDpd());
    // }

    // @Test
    // @DisplayName("""
    //     Case 13 - All Periods Not Paid
    //     Today: 21-12-2024
    //     Schedule:
    //     1. 20-12-2024: Not Paid
    //     2. 20-01-2025: Not Paid
    //     3. 20-02-2025: Not Paid
    //     4. 20-03-2025: Not Paid
    //     5. 20-04-2025: Not Paid
    //     Expected: DPD = 1 (from first maturity date)
    //     """)
    // void case_13_all_not_paid_past_first_maturity() {
    //     List<InstallmentLoanV2> loans = new ArrayList<>();
    //     LocalDate today = LocalDate.of(2024, 12, 21);

    //     // All periods not paid
    //     for (int i = 0; i < 5; i++) {
    //         loans.add(createLoan(
    //             i + 1,
    //             LocalDate.of(2024, 12, 20).plusMonths(i),
    //             BigDecimal.valueOf(50_000_000),
    //             null,
    //             null,
    //             Status.DISBURSEMENT,
    //             today
    //         ));
    //     }

    //     Dpd result = installment.calculate(loans);
    //     assertEquals(1, result.getLatestDpd());
    //     assertEquals(1, result.getMaxDpd());
    // }
}