package com.brunozambiazi.bookingsystem.service

import com.brunozambiazi.bookingsystem.api.dto.CreateBlockRequest
import com.brunozambiazi.bookingsystem.api.dto.UpdateBlockRequest
import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity
import com.brunozambiazi.bookingsystem.domain.model.BlockReason
import com.brunozambiazi.bookingsystem.domain.model.DateRange
import com.brunozambiazi.bookingsystem.domain.repository.BlockRepository
import com.brunozambiazi.bookingsystem.exception.InvalidStateException
import com.brunozambiazi.bookingsystem.exception.OverlapException
import com.brunozambiazi.bookingsystem.service.mapper.BlockMapper
import spock.lang.Specification
import spock.lang.Subject

import static java.time.LocalDate.now

class BlockServiceSpec extends Specification {

    def availabilityService = Mock(AvailabilityService)
    def blockMapper = new BlockMapper()
    def blockRepository = Mock(BlockRepository)
    def lockService = Mock(LockService)
    def propertyService = Mock(PropertyService)

    @Subject
    def service = new BlockService(availabilityService, blockMapper, blockRepository, lockService, propertyService)

    def "should create block when property is active and no overlap"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBlockRequest(propertyId, now(), now().plusDays(1), BlockReason.MAINTENANCE)

        when:
        def result = service.createBlock(request)

        then:
        1 * propertyService.checkActiveProperty(propertyId)
        1 * lockService.acquireLockFor(_ as BlockEntity)
        1 * availabilityService.checkOverlap(_ as BlockEntity)
        1 * blockRepository.save(_ as BlockEntity) >> { BlockEntity it -> it }
        result.propertyId == propertyId
        result.reason == BlockReason.MAINTENANCE
    }

    def "should throw InvalidStateException when creating block for inactive property"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBlockRequest(propertyId, now(), now().plusDays(1), BlockReason.MAINTENANCE)
        propertyService.checkActiveProperty(propertyId) >> { throw new InvalidStateException("") }

        when:
        service.createBlock(request)

        then:
        thrown(InvalidStateException)
    }

    def "should throw OverlapException when creating block with overlap"() {
        given:
        def propertyId = UUID.randomUUID()
        def request = new CreateBlockRequest(propertyId, now(), now().plusDays(1), BlockReason.MAINTENANCE)
        availabilityService.checkOverlap(_ as BlockEntity) >> { throw new OverlapException("") }

        when:
        service.createBlock(request)

        then:
        thrown(OverlapException)
    }

    def "should delete block"() {
        given:
        def blockId = UUID.randomUUID()
        def block = new BlockEntity(id: blockId)
        blockRepository.getById(blockId) >> block

        when:
        service.deleteBlock(blockId)

        then:
        1 * blockRepository.delete(block)
    }

    def "should return block by id"() {
        given:
        def blockId = UUID.randomUUID()
        def block = new BlockEntity(id: blockId, reason: BlockReason.OTHER, period: new DateRange(now(), now().plusDays(1)))
        blockRepository.getById(blockId) >> block

        when:
        def result = service.getBlockById(blockId)

        then:
        result.id == blockId
        result.reason == BlockReason.OTHER
    }

    def "should update block when no overlap"() {
        given:
        def blockId = UUID.randomUUID()
        def block = new BlockEntity(id: blockId, reason: BlockReason.MAINTENANCE)
        def request = new UpdateBlockRequest(now(), now().plusDays(2), BlockReason.OTHER)
        blockRepository.getById(blockId) >> block

        when:
        def result = service.updateBlock(blockId, request)

        then:
        1 * lockService.acquireLockFor(block)
        1 * availabilityService.checkOverlap(block)
        1 * blockRepository.save(block) >> block
        result.reason == BlockReason.OTHER
        result.endAt == request.endAt()
    }

    def "should throw OverlapException when updating block with overlap"() {
        given:
        def blockId = UUID.randomUUID()
        def block = new BlockEntity(id: blockId)
        def request = new UpdateBlockRequest(now(), now().plusDays(2), BlockReason.OTHER)
        blockRepository.getById(blockId) >> block
        availabilityService.checkOverlap(block) >> { throw new OverlapException("") }

        when:
        service.updateBlock(blockId, request)

        then:
        thrown(OverlapException)
    }
}
