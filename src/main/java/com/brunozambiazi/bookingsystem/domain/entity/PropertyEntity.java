package com.brunozambiazi.bookingsystem.domain.entity;

import com.brunozambiazi.bookingsystem.domain.model.PropertyStatus;
import jakarta.persistence.Column;
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

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "properties")
@EqualsAndHashCode(of = "id")
public class PropertyEntity {

    @Id
    private UUID id;

    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public boolean isActive() {
        return status == PropertyStatus.ACTIVE;
    }
}