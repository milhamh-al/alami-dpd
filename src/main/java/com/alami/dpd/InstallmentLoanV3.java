package com.alami.dpd;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class InstallmentLoanV3 {
    private List<InstallmentV3> installments;

    public long calculateLatestDpd(LocalDate calculationDate) {
        return installments.stream()
                .filter(installment -> !installment.isGracePeriod())
                .mapToLong(installment -> installment.calculateDpd(calculationDate))
                .max()
                .orElse(0);
    }

    public long calculateMaxHistoricalDpd(List<LocalDate> historicalDates) {
        return historicalDates.stream()
                .mapToLong(this::calculateLatestDpd)
                .max()
                .orElse(0);
    }
}
