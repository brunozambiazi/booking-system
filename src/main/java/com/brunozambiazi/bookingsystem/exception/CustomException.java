package com.brunozambiazi.bookingsystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected CustomException(String erroCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = erroCode;
        this.httpStatus = httpStatus;
    }
}
