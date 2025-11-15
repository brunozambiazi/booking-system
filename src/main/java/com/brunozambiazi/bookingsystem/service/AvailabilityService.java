package com.brunozambiazi.bookingsystem.service;

import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity;
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import com.brunozambiazi.bookingsystem.domain.repository.BlockRepository;
import com.brunozambiazi.bookingsystem.domain.repository.BookingRepository;
import com.brunozambiazi.bookingsystem.exception.OverlapException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    void checkOverlap(BlockEntity block) {
        UUID propertyId = block.getPropertyId();
        DateRange period = block.getPeriod();
        log.info("Checking overlap for property [{}] on [{}] for a block", propertyId, period);

        if (blockRepository.overlapOtherBlock(block.getId(), propertyId, period.startAt(), period.endAt())) {
            throw new OverlapException("There is a block overlap");
        }

        if (bookingRepository.overlapOtherBooking(null, propertyId, period.startAt(), period.endAt())) {
            throw new OverlapException("There is a booking overlap");
        }
    }

    void checkOverlap(BookingEntity booking) {
        UUID propertyId = booking.getPropertyId();
        DateRange period = booking.getPeriod();
        log.info("Checking overlap for property [{}] on [{}] fora  booking", propertyId, period);

        if (blockRepository.overlapOtherBlock(null, propertyId, period.startAt(), period.endAt())) {
            throw new OverlapException("There is a block overlap");
        }

        if (bookingRepository.overlapOtherBooking(booking.getId(), propertyId, period.startAt(), period.endAt())) {
            throw new OverlapException("There is a booking overlap");
        }
    }
}
