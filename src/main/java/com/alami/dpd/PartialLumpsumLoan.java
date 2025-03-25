package com.alami.dpd;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PartialLumpsumLoan {
    Integer period;
    LocalDate maturityDate;
    LocalDate repaymentDate;
    LocalDate today;
    Status status;
}
