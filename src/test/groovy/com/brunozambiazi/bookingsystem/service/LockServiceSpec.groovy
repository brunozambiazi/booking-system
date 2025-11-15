package com.brunozambiazi.bookingsystem.service

import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity
import com.brunozambiazi.bookingsystem.domain.entity.BookingEntity
import com.brunozambiazi.bookingsystem.domain.repository.PropertyRepository
import spock.lang.Specification
import spock.lang.Subject

class LockServiceSpec extends Specification {

    def propertyRepository = Mock(PropertyRepository)

    @Subject
    def service = new LockService(propertyRepository)

    def "should acquire lock for block"() {
        given:
        def propertyId = UUID.randomUUID()
        def block = new BlockEntity(propertyId: propertyId)

        when:
        service.acquireLockFor(block)

        then:
        1 * propertyRepository.findByIdWithLock(propertyId)
    }

    def "should acquire lock for booking"() {
        given:
        def propertyId = UUID.randomUUID()
        def booking = new BookingEntity(propertyId: propertyId)

        when:
        service.acquireLockFor(booking)

        then:
        1 * propertyRepository.findByIdWithLock(propertyId)
    }
}
