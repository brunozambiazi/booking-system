package com.brunozambiazi.bookingsystem.api.dto;

import com.brunozambiazi.bookingsystem.domain.model.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponse(
    UUID id,
    UUID propertyId,
    LocalDate startAt,
    LocalDate endAt,
    List<GuestDto> guests,
    BookingStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt,
    LocalDateTime rebookedAt
) {}