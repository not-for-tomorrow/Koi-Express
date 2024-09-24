package com.koi_express.exception;


public enum ErrorCode {
    USER_EXISTED(1001, "PhoneNumber already registered"),
    PHONE_NUMBER_INVALID(1002,"Phone number must consist of exactly 10 digits")
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
