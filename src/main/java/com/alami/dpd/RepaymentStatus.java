package com.alami.dpd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepaymentStatus {
    NOT_PAID,
    PARTIAL_REPAYMENT,
    PAID,
    GRACE_PERIOD,
    WRITE_OFF
}
