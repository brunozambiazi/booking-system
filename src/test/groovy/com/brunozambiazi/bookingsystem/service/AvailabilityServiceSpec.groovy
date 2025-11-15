package com.brunozambiazi.bookingsystem.service

import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity
import com.brunozambiazi.bookingsystem.domain.model.DateRange
import com.brunozambiazi.bookingsystem.domain.repository.BlockRepository
import com.brunozambiazi.bookingsystem.domain.repository.BookingRepository
import com.brunozambiazi.bookingsystem.exception.OverlapException
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class AvailabilityServiceSpec extends Specification {

    def bookingRepository = Mock(BookingRepository)
    def blockRepository = Mock(BlockRepository)

    @Subject
    def service = new AvailabilityService(bookingRepository, blockRepository)

    def "checkOverlap for block should not throw exception when no overlap"() {
        given:
        def block = new BlockEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))

        when:
        service.checkOverlap(block)

        then:
        1 * blockRepository.overlapOtherBlock(block.getId(), block.getPropertyId(), block.getPeriod().startAt(), block.getPeriod().endAt()) >> false
        1 * bookingRepository.overlapOtherBooking(null, block.getPropertyId(), block.getPeriod().startAt(), block.getPeriod().endAt()) >> false
        noExceptionThrown()
    }

    def "checkOverlap for block should throw OverlapException when block overlap"() {
        given:
        def block = new BlockEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))
        blockRepository.overlapOtherBlock(block.getId(), block.getPropertyId(), block.getPeriod().startAt(), block.getPeriod().endAt()) >> true

        when:
        service.checkOverlap(block)

        then:
        thrown(OverlapException)
    }

    def "checkOverlap for block should throw OverlapException when booking overlap"() {
        given:
        def block = new BlockEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))
        blockRepository.overlapOtherBlock(block.getId(), block.getPropertyId(), block.getPeriod().startAt(), block.getPeriod().endAt()) >> false
        bookingRepository.overlapOtherBooking(null, block.getPropertyId(), block.getPeriod().startAt(), block.getPeriod().endAt()) >> true

        when:
        service.checkOverlap(block)

        then:
        thrown(OverlapException)
    }

    def "checkOverlap for booking should not throw exception when no overlap"() {
        given:
        def booking = new BookingEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))

        when:
        service.checkOverlap(booking)

        then:
        1 * blockRepository.overlapOtherBlock(null, booking.getPropertyId(), booking.getPeriod().startAt(), booking.getPeriod().endAt()) >> false
        1 * bookingRepository.overlapOtherBooking(booking.getId(), booking.getPropertyId(), booking.getPeriod().startAt(), booking.getPeriod().endAt()) >> false
        noExceptionThrown()
    }

    def "checkOverlap for booking should throw OverlapException when block overlap"() {
        given:
        def booking = new BookingEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))
        blockRepository.overlapOtherBlock(null, booking.getPropertyId(), booking.getPeriod().startAt(), booking.getPeriod().endAt()) >> true

        when:
        service.checkOverlap(booking)

        then:
        thrown(OverlapException)
    }

    def "checkOverlap for booking should throw OverlapException when booking overlap"() {
        given:
        def booking = new BookingEntity(id: UUID.randomUUID(), propertyId: UUID.randomUUID(), period: new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)))
        blockRepository.overlapOtherBlock(null, booking.getPropertyId(), booking.getPeriod().startAt(), booking.getPeriod().endAt()) >> false
        bookingRepository.overlapOtherBooking(booking.getId(), booking.getPropertyId(), booking.getPeriod().startAt(), booking.getPeriod().endAt()) >> true

        when:
        service.checkOverlap(booking)

        then:
        thrown(OverlapException)
    }
}
