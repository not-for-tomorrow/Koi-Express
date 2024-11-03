package com.koi_express.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    int code;
    String message;
    T result;
    String errorDetails;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(200, "Success", result, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, String errorDetails) {
        return new ApiResponse<>(code, message, null, errorDetails);
    }

    public ApiResponse(int code, String message, T result, String errorDetails) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.errorDetails = errorDetails;
    }
}
