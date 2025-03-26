package com.alami.dpd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class Dpd {
    private int latestDpd;
    private int maxDpd;
}
