package com.alami.dpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PartialLumpsumTest {
    private static PartialLumpsum partialLumpsum;

    @BeforeAll
    static void init() {
        partialLumpsum = new PartialLumpsum();
    }

    @Nested
    class BeforeMaturityDate {
        @Test
        @DisplayName("case: before maturity date, not paid")
        void first_payment_not_paid() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 3))
                                                        .today(LocalDate.of(2024, 12, 3))
                                                        .status(RepaymentStatus.NOT_PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 9: before maturity date, first payment, partial lumpsum")
        void first_payment_partial_lumpsum() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 3))
                                                        .today(LocalDate.of(2024, 12, 3))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 10: before maturity date, second payment, partial lumpsum is paid")
        void second_payment_partial_lumpsum_is_paid() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 10))
                                                        .today(LocalDate.of(2024, 12, 18))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan2 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(2)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 20))
                                                        .today(LocalDate.of(2024, 12, 20))
                                                        .status(RepaymentStatus.PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);
            partialLumpsumLoans.add(partialLumpsumLoan2);

            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 11: partial lumpsum before maturity date, fourth payment, partial lumpsum is paid off after maturity date")
        void fourth_payment_partial_lumpsum_is_paid() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 18))
                                                        .today(LocalDate.of(2024, 12, 18))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan2 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(2)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 24))
                                                        .today(LocalDate.of(2024, 12, 24))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan3 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(3)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 30))
                                                        .today(LocalDate.of(2024, 12, 30))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan4 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(4)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2025, 1, 1))
                                                        .today(LocalDate.of(2025, 1, 1))
                                                        .status(RepaymentStatus.PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);
            partialLumpsumLoans.add(partialLumpsumLoan2);
            partialLumpsumLoans.add(partialLumpsumLoan3);
            partialLumpsumLoans.add(partialLumpsumLoan4);

            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(8, dpd.getLatestDpd());
            assertEquals(8, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 12: partial lumpsum before maturity date, fourth payment stuck, write off")
        void fourth_payment_partial_stuck_write_off() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 18))
                                                        .today(LocalDate.of(2024, 12, 18))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan2 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(2)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 24))
                                                        .today(LocalDate.of(2024, 12, 24))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan3 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(3)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2025, 4, 30))
                                                        .today(LocalDate.of(2025, 4, 30))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan4 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(4)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2025, 5, 1))
                                                        .today(LocalDate.of(2025, 5, 1))
                                                        .status(RepaymentStatus.WRITE_OFF)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);
            partialLumpsumLoans.add(partialLumpsumLoan2);
            partialLumpsumLoans.add(partialLumpsumLoan3);
            partialLumpsumLoans.add(partialLumpsumLoan4);

            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(128, dpd.getLatestDpd());
            assertEquals(128, dpd.getMaxDpd());
        }
    }

    @Nested
    class EqualMaturityDate {
        @Test
        @DisplayName("case: equal maturity date, first payment, not paid")
        void first_payment_not_paid() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(null)
                                                        .today(LocalDate.of(2024, 12, 24))
                                                        .status(RepaymentStatus.NOT_PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 6: equal maturity date, first payment, partial lumpsum")
        void first_payment_partial_lumpsum() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2024, 12, 24))
                                                        .today(LocalDate.of(2024, 12, 24))
                                                        .status(RepaymentStatus.PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }
    }

    @Nested
    class AfterMaturityDate {
        @Test
        @DisplayName("case 4: after maturity date, first payment, not partial lumpsum")
        void first_payment_not_partial_lumpsum() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(null)
                                                        .today(LocalDate.of(2025, 1, 1))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(8, dpd.getLatestDpd());
            assertEquals(8, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 8: after maturity date, second payment, partial lumpsum is paid off")
        void second_payment_partial_lumpsum_is_paid() {
            List<PartialLumpsumLoan> partialLumpsumLoans = new ArrayList<>();
            PartialLumpsumLoan partialLumpsumLoan1 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(1)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2025, 1, 1))
                                                        .today(LocalDate.of(2025, 1, 1))
                                                        .status(RepaymentStatus.PARTIAL_REPAYMENT)
                                                        .build();
            PartialLumpsumLoan partialLumpsumLoan2 = PartialLumpsumLoan
                                                        .builder()
                                                        .period(2)
                                                        .maturityDate(LocalDate.of(2024, 12, 24))
                                                        .repaymentDate(LocalDate.of(2025, 1, 8))
                                                        .today(LocalDate.of(2025, 1, 8))
                                                        .status(RepaymentStatus.PAID)
                                                        .build();
            partialLumpsumLoans.add(partialLumpsumLoan1);
            partialLumpsumLoans.add(partialLumpsumLoan2);


            Dpd dpd = partialLumpsum.calculate(partialLumpsumLoans);

            assertEquals(15, dpd.getLatestDpd());
            assertEquals(15, dpd.getMaxDpd());
        }
    }
}
