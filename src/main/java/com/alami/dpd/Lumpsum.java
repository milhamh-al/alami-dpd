package com.alami.dpd;

import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Lumpsum {
    public Dpd calculate(LumpsumLoan lumpsumLoan) {
        // If loan is paid, DPD is 0
        if (lumpsumLoan.getStatus() == Status.READY_FOR_REPAYMENT || 
            lumpsumLoan.getStatus() == Status.REPAYMENT_SUCCESS || 
            lumpsumLoan.getStatus() == Status.READY_FOR_SETTLEMENT || 
            lumpsumLoan.getStatus() == Status.SETTLEMENT_SUCCESS) {
            return new Dpd(0, 0);
        }

        // For unpaid loans, calculate DPD based on today
        Integer dpd = (int) ChronoUnit.DAYS.between(lumpsumLoan.getMaturityDate(), lumpsumLoan.getToday());
        if (dpd < 0) {
            dpd = 0;
        }

        return new Dpd(dpd, dpd);
    }
}
