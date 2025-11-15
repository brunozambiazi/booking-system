package com.brunozambiazi.bookingsystem.domain.model;

import com.brunozambiazi.bookingsystem.exception.InvalidDataException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record DateRange(

    @Column(name = "start_at")
    LocalDate startAt,

    @Column(name = "end_at")
    LocalDate endAt
) {

    public DateRange {
        if (!startAt.isBefore(endAt)) {
            throw new InvalidDataException("Start date must be before than end date");
        }
    }
}
