package com.brunozambiazi.bookingsystem.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record UpdateBookingRequest(

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be today or in the future")
    LocalDate startAt,

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    LocalDate endAt,

    @Valid
    @NotEmpty(message = "At least one guest is required")
    List<GuestDto> guests

) {}