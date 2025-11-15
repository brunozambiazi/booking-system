package com.brunozambiazi.bookingsystem.api.dto;

import com.brunozambiazi.bookingsystem.domain.model.PropertyStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record PropertyResponse(
    UUID id,
    String name,
    String address,
    PropertyStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}