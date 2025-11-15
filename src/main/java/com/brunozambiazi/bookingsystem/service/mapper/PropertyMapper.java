package com.brunozambiazi.bookingsystem.service.mapper;

import com.brunozambiazi.bookingsystem.api.dto.PropertyResponse;
import com.brunozambiazi.bookingsystem.domain.entity.PropertyEntity;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    public PropertyResponse toResponse(PropertyEntity entity) {
        return new PropertyResponse(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
