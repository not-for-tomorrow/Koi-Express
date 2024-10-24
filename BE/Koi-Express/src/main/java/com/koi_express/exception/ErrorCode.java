package com.koi_express.exception;

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
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
