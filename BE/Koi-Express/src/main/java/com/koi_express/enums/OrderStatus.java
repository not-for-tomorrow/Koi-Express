package com.koi_express.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    SEARCHING_FOR_DELIVERY_STAFF,
    ASSIGNED,
    PICKING_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELED,

    COMMIT_FEE_PENDING,
    COMMIT_FEE_PAID,
    PAYMENT_PENDING;

}
