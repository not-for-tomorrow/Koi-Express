package com.koi_express.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KoiDataDTO {

    private String koiType;

    private BigDecimal koiSize;

    private Integer koiQuantity;
}
