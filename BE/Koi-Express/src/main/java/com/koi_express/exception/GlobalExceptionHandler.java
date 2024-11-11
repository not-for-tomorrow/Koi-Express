package com.koi_express.exception;

import java.util.HashMap;
import java.util.Map;

import com.koi_express.dto.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException exception) {
        logger.error("Runtime exception occurred: {}", exception.getMessage(), exception);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(1001);
        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        logger.warn("Application exception occurred: {}", exception.getMessage(), exception);
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.info("Validation error(s) occurred: {}", errors);
        ApiResponse<Map<String, String>> response = new ApiResponse<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");
        response.setResult(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        logger.info("Entity not found: {}", ex.getMessage());
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage("Resource not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("An unexpected error occurred. Please try again later.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ApiResponse<>(
                        HttpStatus.PAYLOAD_TOO_LARGE.value(), "File size exceeds the maximum limit!", null));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String errorMessage = String.format("Required parameter '%s' is missing", name);
        ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null);
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
