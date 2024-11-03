package com.koi_express.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class KoiDataDTO {

    private String koiType;
    private BigDecimal koiSize;
    private Integer koiQuantity;
}
