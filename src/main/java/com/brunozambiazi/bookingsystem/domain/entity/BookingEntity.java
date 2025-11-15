package com.brunozambiazi.bookingsystem.domain.entity;

import com.brunozambiazi.bookingsystem.domain.model.BookingStatus;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "bookings")
@EqualsAndHashCode(of = "id")
public class BookingEntity {

    @Id
    private UUID id;

    @Column(name = "property_id")
    private UUID propertyId;

    @Embedded
    private DateRange period;

    @Column(columnDefinition = "json")
    private String guests;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "rebooked_at")
    private LocalDateTime rebookedAt;


    public void cancel() {
        status = BookingStatus.CANCELLED;
        updatedAt = now();
        cancelledAt = now();
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public boolean isCancellable() {
        return isActive();
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    public boolean isRebookable() {
        return isCancelled();
    }

    public void rebook() {
        status = BookingStatus.ACTIVE;
        updatedAt = now();
        rebookedAt = now();
    }

    public static BookingEntity newBooking() {
        BookingEntity booking = new BookingEntity();
        booking.setId(randomUUID());
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setCreatedAt(now());
        return booking;
    }
}