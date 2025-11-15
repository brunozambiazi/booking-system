package com.brunozambiazi.bookingsystem.service;

import com.brunozambiazi.bookingsystem.api.dto.PropertyResponse;
import com.brunozambiazi.bookingsystem.domain.entity.PropertyEntity;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import com.brunozambiazi.bookingsystem.domain.repository.PropertyRepository;
import com.brunozambiazi.bookingsystem.exception.InvalidStateException;
import com.brunozambiazi.bookingsystem.service.mapper.PropertyMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;

    public void checkActiveProperty(UUID propertyId) {
        PropertyEntity entity = propertyRepository.getById(propertyId);

        if (!entity.isActive()) {
            throw new InvalidStateException("Property is not active");
        }
    }

    public List<PropertyResponse> findAvailableProperties(DateRange period) {
        return propertyRepository.findAllAvailable(period.startAt(), period.endAt())
                .stream()
                .map(propertyMapper::toResponse)
                .toList();
    }
}