package com.brunozambiazi.bookingsystem.service.mapper;

import com.brunozambiazi.bookingsystem.api.dto.BlockResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBlockRequest;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBlockRequest;
import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import org.springframework.stereotype.Component;

import static com.brunozambiazi.bookingsystem.domain.entity.BlockEntity.newBlock;
import static java.time.LocalDateTime.now;

@Component
public class BlockMapper {

    public BlockEntity toEntity(CreateBlockRequest request) {
        BlockEntity entity = newBlock();
        entity.setPropertyId(request.propertyId());
        entity.setPeriod(new DateRange(request.startAt(), request.endAt()));
        entity.setReason(request.reason());
        return entity;
    }

    public BlockResponse toResponse(BlockEntity entity) {
        return new BlockResponse(
                entity.getId(),
                entity.getPropertyId(),
                entity.getPeriod().startAt(),
                entity.getPeriod().endAt(),
                entity.getReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public void updateEntity(BlockEntity entity, UpdateBlockRequest request) {
        entity.setPeriod(new DateRange(request.startAt(), request.endAt()));
        entity.setReason(request.reason());
        entity.setUpdatedAt(now());
    }
}
