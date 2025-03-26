package com.alami.dpd;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class LumpsumLoan {
    LocalDate repaymentDate;
    LocalDate maturityDate;
    LocalDate today;
    RepaymentStatus status;
}
