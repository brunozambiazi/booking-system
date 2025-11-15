package com.brunozambiazi.bookingsystem.service

import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest
import com.brunozambiazi.bookingsystem.api.dto.GuestDto
import com.brunozambiazi.bookingsystem.api.dto.UpdateBookingRequest
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity
import com.brunozambiazi.bookingsystem.domain.model.BookingStatus
import com.brunozambiazi.bookingsystem.domain.model.DateRange
import com.brunozambiazi.bookingsystem.domain.repository.BookingRepository
import com.brunozambiazi.bookingsystem.exception.InvalidStateException
import com.brunozambiazi.bookingsystem.exception.OverlapException
import com.brunozambiazi.bookingsystem.service.mapper.BookingMapper
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject

import static java.time.LocalDate.now

class BookingServiceSpec extends Specification {

    def objectMapper = new ObjectMapper()
    def availabilityService = Mock(AvailabilityService)
    def bookingMapper = new BookingMapper(objectMapper)
    def bookingRepository = Mock(BookingRepository)
    def lockService = Mock(LockService)
    def propertyService = Mock(PropertyService)

    @Subject
    def service = new BookingService(availabilityService, bookingMapper, bookingRepository, lockService, propertyService)

    def "should cancel booking when it is cancellable"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.ACTIVE)
        bookingRepository.getById(bookingId) >> booking

        when:
        service.cancelBooking(bookingId)

        then:
        booking.getStatus() == BookingStatus.CANCELLED
        1 * bookingRepository.save(booking)
    }

    def "should throw InvalidStateException when cancelling a non-cancellable booking"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.CANCELLED)
        bookingRepository.getById(bookingId) >> booking

        when:
        service.cancelBooking(bookingId)

        then:
        thrown(InvalidStateException)
    }

    def "should create booking when property is active and no overlap"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBookingRequest(propertyId, now(), now().plusDays(1), [new GuestDto("John", "john.doe@test.com")])

        when:
        def result = service.createBooking(request)

        then:
        1 * propertyService.checkActiveProperty(propertyId)
        1 * lockService.acquireLockFor(_ as BookingEntity)
        1 * availabilityService.checkOverlap(_ as BookingEntity)
        1 * bookingRepository.save(_ as BookingEntity) >> { BookingEntity it -> it }
        result.propertyId == propertyId
        result.guests[0].name == "John"
    }

    def "should throw InvalidStateException when creating booking for inactive property"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBookingRequest(propertyId, now(), now().plusDays(1), [new GuestDto("John", "john.doe@test.com")])
        propertyService.checkActiveProperty(propertyId) >> { throw new InvalidStateException("") }

        when:
        service.createBooking(request)

        then:
        thrown(InvalidStateException)
    }

    def "should throw OverlapException when creating booking with overlap"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBookingRequest(propertyId, now(), now().plusDays(1), [new GuestDto("John", "john.doe@test.com")])
        availabilityService.checkOverlap(_ as BookingEntity) >> { throw new OverlapException("") }

        when:
        service.createBooking(request)

        then:
        thrown(OverlapException)
    }

    def "should delete booking"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId)
        bookingRepository.getById(bookingId) >> booking

        when:
        service.deleteBooking(bookingId)

        then:
        1 * bookingRepository.delete(booking)
    }

    def "should return booking by id"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.ACTIVE, period: new DateRange(now(), now().plusDays(1)), guests: "[]")
        bookingRepository.getById(bookingId) >> booking

        when:
        def result = service.getBookingById(bookingId)

        then:
        result.id == bookingId
        result.status == BookingStatus.ACTIVE
    }

    def "should rebook booking when it is rebookable"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.CANCELLED)
        bookingRepository.getById(bookingId) >> booking

        when:
        service.rebookBooking(bookingId)

        then:
        booking.getStatus() == BookingStatus.ACTIVE
        1 * bookingRepository.save(booking)
    }

    def "should throw InvalidStateException when rebooking a non-rebookable booking"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.ACTIVE)
        bookingRepository.getById(bookingId) >> booking

        when:
        service.rebookBooking(bookingId)

        then:
        thrown(InvalidStateException)
    }

    def "should update booking when no overlap"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId, status: BookingStatus.ACTIVE)
        def request = new UpdateBookingRequest(now(), now().plusDays(2), [])
        bookingRepository.getById(bookingId) >> booking

        when:
        def result = service.updateBooking(bookingId, request)

        then:
        1 * lockService.acquireLockFor(booking)
        1 * availabilityService.checkOverlap(booking)
        1 * bookingRepository.save(booking) >> booking
        result.endAt == request.endAt()
    }

    def "should throw OverlapException when updating booking with overlap"() {
        given:
        def bookingId = UUID.randomUUID()
        def booking = new BookingEntity(id: bookingId)
        def request = new UpdateBookingRequest(now(), now().plusDays(2), [])
        bookingRepository.getById(bookingId) >> booking
        availabilityService.checkOverlap(booking) >> { throw new OverlapException("") }

        when:
        service.updateBooking(bookingId, request)

        then:
        thrown(OverlapException)
    }
}