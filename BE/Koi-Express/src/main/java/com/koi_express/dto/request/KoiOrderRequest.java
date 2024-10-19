package com.koi_express.dto.request;

import com.koi_express.enums.KoiType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KoiOrderRequest {

    private KoiType koiType;
    private int koiQuantity;
    private double koiSize;
    private double distance;
    private double commitmentFee;
}
