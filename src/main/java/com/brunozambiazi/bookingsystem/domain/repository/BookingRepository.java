package com.brunozambiazi.bookingsystem.domain.repository;

import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity;
import com.brunozambiazi.bookingsystem.exception.NotFoundException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    default BookingEntity getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Query(value = """
        SELECT EXISTS(
            SELECT 1
            FROM bookings
            WHERE (:bookingId IS NULL OR id != :bookingId)
              AND property_id = :propertyId
              AND status = 'ACTIVE'
              AND start_at < :endAt
              AND end_at > :startAt
          )
    """, nativeQuery = true)
    boolean overlapOtherBooking(
            @Param("bookingId") UUID bookingId,
            @Param("propertyId") UUID propertyId,
            @Param("startAt") LocalDate startAt,
            @Param("endAt") LocalDate endAt);
}