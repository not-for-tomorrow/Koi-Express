package com.koi_express.exception;


public enum ErrorCode {
    USER_EXISTED(1001, "PhoneNumber already registered"),
    PHONE_NUMBER_INVALID(1002,"Phone number must consist of exactly 10 digits"),
    DATABASE_ERROR(1003, "Database connection error"),
    AUTHENTICATION_FAILED(1004, "Authentication failed"),
    ACCESS_DENIED(1005, "Access denied"),
    RESOURCE_NOT_FOUND(1006, "Resource not found"),
    PASSWORD_INCORRECT(1007, "Invalid password"),
    SUCCESS(1007,"Register successfully"),
    CUSTOMER_NOT_FOUND(1008,"Customer not found"),
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
