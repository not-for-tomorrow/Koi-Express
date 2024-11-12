package com.koi_express.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

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

    public ApiResponse(int code, String message) {
        this(code, message, null);
    }

    public ApiResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> ApiResponse<T> success(String message, T result) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, result);
    }

    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), message, null);
    }

    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }
}
