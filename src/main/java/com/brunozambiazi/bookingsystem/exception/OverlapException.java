package com.brunozambiazi.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class OverlapException extends CustomException {

    public OverlapException(String message) {
        super("DATE_OVERLAP", message, HttpStatus.CONFLICT);
    }
}
