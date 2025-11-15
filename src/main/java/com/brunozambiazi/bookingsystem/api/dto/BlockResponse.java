package com.brunozambiazi.bookingsystem.api.dto;

import com.brunozambiazi.bookingsystem.domain.model.BlockReason;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BlockResponse(
    UUID id,
    UUID propertyId,
    LocalDate startAt,
    LocalDate endAt,
    BlockReason reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}