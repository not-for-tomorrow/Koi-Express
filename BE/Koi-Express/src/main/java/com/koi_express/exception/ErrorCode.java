package com.koi_express.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "PhoneNumber already registered"),
    PASSWORD_INCORRECT(1002, "Invalid password"),
    CUSTOMER_NOT_FOUND(1003, "Customer not found"),
    ORDER_CREATION_FAILED(1004, "Order creation failed"),
    ORDER_NOT_FOUND(1005, "Order not found"),
    ORDER_ALREADY_PROCESSED(1006, "Order already processed"),
    EMAIL_SENDING_FAILED(1007, "Email sending failed"),
    EMAIL_ALREADY_EXISTS(1008, "Email already exists"),
    INVALID_ROLE(1009, "Invalid role"),
    NO_AVAILABLE_STAFF(1010, "No available staff"),
    ORDER_ALREADY_ASSIGNED(1011, "Order already assigned"),
    STAFF_ASSIGNMENT_FAILED(1012, "Staff assignment failed"),
    ORDER_HISTORY_RETRIEVAL_FAILED(1013, "Order history retrieval failed"),
    JWT_PARSING_FAILED(1014, "JWT parsing failed"),
    ORDER_INVALID(1015, "Order has invalid status"),
    ORDER_NOT_ASSIGNED(1016, "Order not assigned to staff"),
    ORDER_RETRIEVAL_FAILED(1017, "Order retrieval failed"),
    ORDER_NOT_IN_TRANSIT(1018, "Order not in transit"),
    MISSING_ORDER_DETAILS(1019, "Order details missing"),
    STAFF_NOT_FOUND(1020, "Staff not found"),
    INVALID_LEVEL(1021, "Invalid level"),
    ORDER_ALREADY_DELIVERED(1022, "Order already delivered"),
    INVALID_ORDER_STATUS(1023, "Invalid order status"),
    ORDER_PRICE_CALCULATION_FAILED(1024, "Order price calculation failed"),
    ORDER_DELIVERY_FAILED(1025, "Order delivery failed"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;
    private final String message;
}
