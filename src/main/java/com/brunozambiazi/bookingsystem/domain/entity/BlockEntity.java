package com.brunozambiazi.bookingsystem.domain.entity;

import com.brunozambiazi.bookingsystem.domain.model.BlockReason;
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
@Table(name = "blocks")
@EqualsAndHashCode(of = "id")
public class BlockEntity {

    @Id
    private UUID id;

    @Column(name = "property_id")
    private UUID propertyId;

    @Embedded
    private DateRange period;

    @Enumerated(EnumType.STRING)
    private BlockReason reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public static BlockEntity newBlock() {
        BlockEntity block = new BlockEntity();
        block.setId(randomUUID());
        block.setCreatedAt(now());
        return block;
    }
}