package com.brunozambiazi.bookingsystem.service;

import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity;
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity;
import com.brunozambiazi.bookingsystem.domain.repository.PropertyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class LockService {

    private final PropertyRepository propertyRepository;

    void acquireLockFor(BlockEntity block) {
        UUID propertyId = block.getPropertyId();
        log.info("Acquiring lock for block property [{}]", propertyId);
        propertyRepository.findByIdWithLock(propertyId);
    }

    void acquireLockFor(BookingEntity booking) {
        UUID propertyId = booking.getPropertyId();
        log.info("Acquiring lock for booking property [{}]", propertyId);
        propertyRepository.findByIdWithLock(propertyId);
    }
}
