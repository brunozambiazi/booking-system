package com.brunozambiazi.bookingsystem.api.error;

import com.brunozambiazi.bookingsystem.exception.CustomException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                null
        );

        log.error("Exception: "+error, ex);
        return status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleBodyMissing(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "Request body is invalid",
                ex.getMessage()
        );

        log.error("Exception: "+error, ex);
        return status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Some fields are invalid",
            Map.of("fields", fieldErrors)
        );

        log.error("Exception: "+error, ex);
        return status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(constraint ->
            violations.put(constraint.getPropertyPath().toString(), constraint.getMessage())
        );

        ErrorResponse error = new ErrorResponse(
            "CONSTRAINT_VIOLATION",
            "Invalid request parameters",
            Map.of("violations", violations)
        );

        log.error("Exception: "+error, ex);
        return status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "UNEXPECTED_ERROR",
            ex.getMessage(),
            null
        );

        log.error("Exception: "+error, ex);
        return status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
