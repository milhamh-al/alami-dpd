package com.alami.dpd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    DISBURSEMENT(27),
    READY_FOR_REPAYMENT(31),
    WRITE_OFF(31),
    REPAYMENT_SUCCESS(32),
    READY_FOR_SETTLEMENT(33),
    SETTLEMENT_SUCCESS(34),
    READY_FOR_PARTIAL_REPAYMENT(54),
    PARTIAL_REPAYMENT_SUCCESS(55);

    private final Integer id;
}
