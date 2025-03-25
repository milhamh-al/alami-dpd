package com.alami.dpd;

import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Lumpsum {
    public Dpd calculate(LumpsumLoan lumpsumLoan) {
        Integer dpd = (int) ChronoUnit.DAYS.between(lumpsumLoan.getMaturityDate(), lumpsumLoan.getToday());
        if (dpd < 0) {
            dpd = 0;
        }
        

        return new Dpd(dpd, dpd);
    }
}
