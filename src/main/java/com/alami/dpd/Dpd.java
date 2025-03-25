package com.alami.dpd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Dpd {
    private Integer dpdTerakhir;
    private Integer dpdMax;
}
