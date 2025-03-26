package com.alami.dpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LumpsumTest {
    private static Lumpsum lumpsum;

    @BeforeAll
    static void init() {
        lumpsum = new Lumpsum();
    }

    @Nested
    class BeforeMaturityDate {
        @Test
        @DisplayName("case 1: before maturity date, not paid off")
        void not_paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = null;
            LocalDate today = LocalDate.of(2024, 12, 3);
            RepaymentStatus status = RepaymentStatus.NOT_PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 5: before maturity date, paid off")
        void paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = LocalDate.of(2024, 12, 20);
            LocalDate today = LocalDate.of(2024, 12, 20);
            RepaymentStatus status = RepaymentStatus.PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );

            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }
    }

    @Nested
    class EqualMaturityDate {
        @Test
        @DisplayName("case 2: equal maturity date, not paid off")
        void not_paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = null;
            LocalDate today = LocalDate.of(2024, 12, 24);
            RepaymentStatus status = RepaymentStatus.NOT_PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );
            
            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 6: equal maturity date, paid off")
        void paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = LocalDate.of(2024, 12, 24);
            LocalDate today = LocalDate.of(2024, 12, 24);
            RepaymentStatus status = RepaymentStatus.PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );
            
            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }
    }

    @Nested
    class AfterMaturityDate {
        @Test
        @DisplayName("case 3: past maturity date, not paid off")
        void not_paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = null;
            LocalDate today = LocalDate.of(2024, 12, 31);
            RepaymentStatus status = RepaymentStatus.NOT_PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );
            
            assertEquals(7, dpd.getLatestDpd());
            assertEquals(7, dpd.getMaxDpd());
        }

        @Test
        @DisplayName("case 7: past maturity date, paid off")
        void paid() {
            LocalDate maturityDate = LocalDate.of(2024, 12, 24);
            LocalDate repaymentDate = LocalDate.of(2024, 12, 31);
            LocalDate today = LocalDate.of(2024, 12, 31);
            RepaymentStatus status = RepaymentStatus.PAID;

            Dpd dpd = lumpsum.calculate(
                LumpsumLoan
                    .builder()
                    .maturityDate(maturityDate)
                    .repaymentDate(repaymentDate)
                    .today(today)
                    .status(status)
                    .build()
            );
            
            assertEquals(0, dpd.getLatestDpd());
            assertEquals(0, dpd.getMaxDpd());
        }
    }
}
