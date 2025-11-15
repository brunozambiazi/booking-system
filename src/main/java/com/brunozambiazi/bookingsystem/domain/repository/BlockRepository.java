package com.brunozambiazi.bookingsystem.domain.repository;

import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity;
import com.brunozambiazi.bookingsystem.exception.NotFoundException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, UUID> {

    default BlockEntity getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Block not found"));
    }

    @Query(value = """
        SELECT EXISTS(
            SELECT 1
            FROM blocks
            WHERE (:blockId IS NULL OR id != :blockId)
              AND property_id = :propertyId
              AND start_at < :endAt
              AND end_at > :startAt
          )
    """, nativeQuery = true)
    boolean overlapOtherBlock(
            @Param("blockId") UUID blockId,
            @Param("propertyId") UUID propertyId,
            @Param("startAt") LocalDate startAt,
            @Param("endAt") LocalDate endAt);
}