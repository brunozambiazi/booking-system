package com.brunozambiazi.bookingsystem.api.error;

record ErrorResponse(
        String code,
        String message,
        Object details
) {
}
