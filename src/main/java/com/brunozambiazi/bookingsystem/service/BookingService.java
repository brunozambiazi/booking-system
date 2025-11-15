package com.brunozambiazi.bookingsystem.service;

import com.brunozambiazi.bookingsystem.api.dto.BookingResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBookingRequest;
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity;
import com.brunozambiazi.bookingsystem.domain.repository.BookingRepository;
import com.brunozambiazi.bookingsystem.exception.InvalidStateException;
import com.brunozambiazi.bookingsystem.service.mapper.BookingMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final AvailabilityService availabilityService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final LockService lockService;
    private final PropertyService propertyService;

    @Transactional
    public void cancelBooking(UUID bookingId) {
        BookingEntity booking = bookingRepository.getById(bookingId);
        log.info("Cancelling booking: [{}]", booking);

        if (!booking.isCancellable()) {
            throw new InvalidStateException("Booking cannot be cancelled");
        }

        booking.cancel();
        bookingRepository.save(booking);
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        propertyService.checkActiveProperty(request.propertyId());

        BookingEntity booking = bookingMapper.toEntity(request);
        log.info("Creating booking: [{}]", booking);
        validateAndSave(booking);

        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public void deleteBooking(UUID bookingId) {
        BookingEntity booking = bookingRepository.getById(bookingId);
        log.info("Deleting booking: [{}]", booking);
        bookingRepository.delete(booking);
    }

    public BookingResponse getBookingById(UUID bookingId) {
        BookingEntity booking = bookingRepository.getById(bookingId);
        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public void rebookBooking(UUID bookingId) {
        BookingEntity booking = bookingRepository.getById(bookingId);
        log.info("Rebooking booking: [{}]", booking);

        if (!booking.isRebookable()) {
            throw new InvalidStateException("Booking cannot be rebooked");
        }

        booking.rebook();
        bookingRepository.save(booking);
    }

    @Transactional
    public BookingResponse updateBooking(UUID bookingId, UpdateBookingRequest request) {
        BookingEntity booking = bookingRepository.getById(bookingId);
        bookingMapper.updateEntity(booking, request);
        log.info("Updating booking: [{}]", booking);
        validateAndSave(booking);

        return bookingMapper.toResponse(booking);
    }

    private void validateAndSave(BookingEntity booking) {
        lockService.acquireLockFor(booking);
        availabilityService.checkOverlap(booking);

        log.info("Saving booking: [{}]", booking);
        bookingRepository.save(booking);
    }
}
