package com.brunozambiazi.bookingsystem.domain.repository;

import com.brunozambiazi.bookingsystem.domain.entity.PropertyEntity;
import com.brunozambiazi.bookingsystem.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {

    @Query(value = """
        SELECT prp.*
        FROM properties prp
        WHERE prp.status = 'ACTIVE'
          AND NOT EXISTS (
              SELECT 1
              FROM bookings bkn
              WHERE bkn.property_id = prp.id
                AND bkn.status = 'ACTIVE'
                AND bkn.start_at < :endAt
                AND bkn.end_at > :startAt
            )
          AND NOT EXISTS (
              SELECT 1
              FROM blocks blc
              WHERE blc.property_id = prp.id
                AND blc.start_at < :endAt
                AND blc.end_at > :startAt
            )
    """, nativeQuery = true)
    List<PropertyEntity> findAllAvailable(
            @Param("startAt") LocalDate startAt,
            @Param("endAt") LocalDate endAt);

    @Lock(PESSIMISTIC_WRITE)
    @Query("FROM PropertyEntity WHERE id = :id")
    PropertyEntity findByIdWithLock(@Param("id") UUID id);

    default PropertyEntity getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Property not found"));
    }
}