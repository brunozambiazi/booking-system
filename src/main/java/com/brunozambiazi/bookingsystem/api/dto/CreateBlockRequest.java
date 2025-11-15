package com.brunozambiazi.bookingsystem.api.dto;

import com.brunozambiazi.bookingsystem.domain.model.BlockReason;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBlockRequest(

    @NotNull(message = "Property ID is required")
    UUID propertyId,

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be today or in the future")
    LocalDate startAt,

    @NotNull(message = "End date is required")
    LocalDate endAt,

    @NotNull(message = "Reason is required")
    BlockReason reason

) {}