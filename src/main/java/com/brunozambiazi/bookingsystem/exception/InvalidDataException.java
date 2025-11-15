package com.brunozambiazi.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends CustomException {

    public InvalidDataException(String message) {
        super("INVALID_DATA", message, HttpStatus.BAD_REQUEST);
    }
}
