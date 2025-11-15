package com.brunozambiazi.bookingsystem.api;

import com.brunozambiazi.bookingsystem.api.dto.BookingResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBookingRequest;
import com.brunozambiazi.bookingsystem.service.BookingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
class BookingController {

    private final BookingService bookingService;

    @PostMapping
    ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid CreateBookingRequest request) {
        log.info("Received request to create booking: [{}]", request);

        BookingResponse response = bookingService.createBooking(request);
        log.info("Create booking finished: [{}]", response);

        return created(URI.create("/api/bookings/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        log.info("Received request to get booking: [{}]", id);

        BookingResponse response = bookingService.getBookingById(id);
        log.info("Get booking finished: [{}]", response);

        return ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<BookingResponse> updateBooking(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBookingRequest request
    ) {
        log.info("Received request to update booking [{}]: [{}]", id, request);

        BookingResponse response = bookingService.updateBooking(id, request);
        log.info("Update booking finished: [{}]", response);

        return ok(response);
    }

    @PutMapping("/{id}/cancel")
    ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        log.info("Received request to cancel booking: [{}]", id);

        bookingService.cancelBooking(id);
        log.info("Cancel booking finished");

        return noContent().build();
    }

    @PutMapping("/{id}/rebook")
    ResponseEntity<Void> rebookBooking(@PathVariable UUID id) {
        log.info("Received request to rebook booking: [{}]", id);

        bookingService.rebookBooking(id);
        log.info("Rebook booking finished");

        return noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBooking(@PathVariable UUID id) {
        log.info("Received request to delete booking: [{}]", id);

        bookingService.deleteBooking(id);
        log.info("Cancel booking finished");

        return noContent().build();
    }
}