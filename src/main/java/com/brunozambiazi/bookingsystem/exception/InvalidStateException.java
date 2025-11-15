package com.brunozambiazi.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends CustomException {

    public InvalidStateException(String message) {
        super("INVALID_STATE", message, HttpStatus.PRECONDITION_FAILED);
    }
}
