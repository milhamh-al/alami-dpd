package com.alami.dpd;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PartialLumpsum {
    public Dpd calculate(List<PartialLumpsumLoan> partialLumpsumLoans) {
        /**
         * can change the order key with period / repayment date / maturity date.
         * the goal is to get the last partial lumpsum data.
         * because we want to get the latest partial lumpsum dpd.
         */
        PartialLumpsumLoan latestPartialLumpsumLoan = partialLumpsumLoans
                                                        .stream()
                                                        .max(Comparator.comparing(PartialLumpsumLoan::getPeriod))
                                                        .orElse(null);

        Integer dpd = (int) ChronoUnit.DAYS.between(
                latestPartialLumpsumLoan.getMaturityDate(), 
                latestPartialLumpsumLoan.getToday());
        if (dpd < 0) {
            dpd = 0;
        }
        

        return new Dpd(dpd, dpd);
    }
}
