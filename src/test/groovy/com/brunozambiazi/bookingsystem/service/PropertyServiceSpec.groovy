package com.brunozambiazi.bookingsystem.service

import com.brunozambiazi.bookingsystem.domain.entity.PropertyEntity
import com.brunozambiazi.bookingsystem.domain.model.DateRange
import com.brunozambiazi.bookingsystem.domain.model.PropertyStatus
import com.brunozambiazi.bookingsystem.domain.repository.PropertyRepository
import com.brunozambiazi.bookingsystem.exception.InvalidStateException
import com.brunozambiazi.bookingsystem.service.mapper.PropertyMapper
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class PropertyServiceSpec extends Specification {

    def propertyMapper = new PropertyMapper()
    def propertyRepository = Mock(PropertyRepository)

    @Subject
    def service = new PropertyService(propertyMapper, propertyRepository)

    def "should not throw exception when property is active"() {
        given:
        def propertyId = UUID.randomUUID()
        def property = new PropertyEntity(id: propertyId, status: PropertyStatus.ACTIVE)

        when:
        service.checkActiveProperty(propertyId)

        then:
        1 * propertyRepository.getById(propertyId) >> property
        noExceptionThrown()
    }

    def "should throw InvalidStateException when property is not active"() {
        given:
        def propertyId = UUID.randomUUID()
        def property = new PropertyEntity(id: propertyId, status: PropertyStatus.INACTIVE)
        propertyRepository.getById(propertyId) >> property

        when:
        service.checkActiveProperty(propertyId)

        then:
        thrown(InvalidStateException)
    }

    def "should return available properties"() {
        given:
        def startAt = LocalDate.now()
        def endAt = LocalDate.now().plusDays(1)
        def period = new DateRange(startAt, endAt)
        def property = new PropertyEntity(id: UUID.randomUUID(), name: "Test Property", status: PropertyStatus.ACTIVE)
        propertyRepository.findAllAvailable(startAt, endAt) >> [property]

        when:
        def result = service.findAvailableProperties(period)

        then:
        result.size() == 1
        result[0].id == property.id
        result[0].name == property.name
    }

    def "should return empty list when no properties are available"() {
        given:
        def startAt = LocalDate.now()
        def endAt = LocalDate.now().plusDays(1)
        def period = new DateRange(startAt, endAt)
        propertyRepository.findAllAvailable(startAt, endAt) >> []

        when:
        def result = service.findAvailableProperties(period)

        then:
        result.isEmpty()
    }
}
