package com.brunozambiazi.bookingsystem.service.mapper;

import com.brunozambiazi.bookingsystem.api.dto.BookingResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest;
import com.brunozambiazi.bookingsystem.api.dto.GuestDto;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBookingRequest;
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import static com.brunozambiazi.bookingsystem.domain.entity.BookingEntity.newBooking;
import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ObjectMapper objectMapper;

    public BookingEntity toEntity(CreateBookingRequest request) {
        BookingEntity entity = newBooking();
        entity.setPropertyId(request.propertyId());
        entity.setPeriod(new DateRange(request.startAt(), request.endAt()));
        entity.setGuests(convertToString(request.guests()));
        return entity;
    }

    public BookingResponse toResponse(BookingEntity entity) {
        return new BookingResponse(
                entity.getId(),
                entity.getPropertyId(),
                entity.getPeriod().startAt(),
                entity.getPeriod().endAt(),
                convertToGuests(entity.getGuests()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCancelledAt(),
                entity.getRebookedAt());
    }

    public void updateEntity(BookingEntity entity, UpdateBookingRequest request) {
        entity.setPeriod(new DateRange(request.startAt(), request.endAt()));
        entity.setGuests(convertToString(request.guests()));
        entity.setUpdatedAt(now());
    }

    @SneakyThrows
    private List<GuestDto> convertToGuests(String json) {
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    @SneakyThrows
    private String convertToString(List<GuestDto> guests) {
        return objectMapper.writeValueAsString(guests);
    }
}
