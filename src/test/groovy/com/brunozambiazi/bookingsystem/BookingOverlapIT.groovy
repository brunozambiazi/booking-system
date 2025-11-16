package com.brunozambiazi.bookingsystem

import com.brunozambiazi.bookingsystem.api.dto.CreateBlockRequest
import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest
import com.brunozambiazi.bookingsystem.api.dto.GuestDto
import com.brunozambiazi.bookingsystem.api.dto.UpdateBookingRequest
import com.brunozambiazi.bookingsystem.domain.model.BlockReason
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static java.time.LocalDate.parse
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BookingOverlapIT extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    def "should not allow new block to overlap booking or block"() {
        given: "An existing property id"
        def propertyId = UUID.fromString("bdad3baf-f65d-41eb-a190-677679dec204")

        when: "Trying to create a block within another block period"
        def overlappingBlock = new CreateBlockRequest(propertyId, parse("2026-01-01"), parse("2026-01-05"), BlockReason.OTHER)
        def blockResult = mockMvc.perform(post("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBlock)))

        then: "Should fail"
        blockResult.andExpect(status().isConflict())

        when: "Trying to create a block within a booking period"
        def overlappingBooking = new CreateBlockRequest(propertyId, parse("2026-01-15"), parse("2026-01-25"), BlockReason.OTHER)
        def bookingResult = mockMvc.perform(post("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBooking)))

        then: "Should fail"
        bookingResult.andExpect(status().isConflict())
    }

    def "should not allow new booking to overlap booking or block"() {
        given: "An existing property id"
        def propertyId = UUID.fromString("bdad3baf-f65d-41eb-a190-677679dec204")

        when: "Trying to create a booking within a block period"
        def overlappingBlock = new CreateBookingRequest(propertyId, parse("2026-01-01"), parse("2026-01-05"), [new GuestDto("Fail", "fail1@test.com")])
        def blockResult = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBlock)))

        then: "Should fail"
        blockResult.andExpect(status().isConflict())

        when: "Trying to create a booking within another booking period"
        def overlappingBooking = new CreateBookingRequest(propertyId, parse("2026-01-15"), parse("2026-01-25"), [new GuestDto("Fail", "fail1@test.com")])
        def bookingResult = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBooking)))

        then: "Should fail"
        bookingResult.andExpect(status().isConflict())
    }

    def "should not allow rebook if it overlap other booking"() {
        given: "An existing property id and booking id"
        def propertyId = UUID.fromString("bdad3baf-f65d-41eb-a190-677679dec204")
        def bookingId = UUID.fromString("f170594d-7476-4b0a-b272-0132f1277f1c")

        when: "Original booking is cancelled"
        mockMvc.perform(put("/api/bookings/${bookingId}/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())

        and: "New booking is created for that period"
        def createBookingRequest = new CreateBookingRequest(propertyId, parse('2026-01-15'), parse('2026-01-25'), [new GuestDto("Doe", "doe.smith@test.com")])
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isCreated())

        and: "Original booking is rebooked"
        def rebookResult = mockMvc.perform(put("/api/bookings/${bookingId}/rebook")
                .contentType(MediaType.APPLICATION_JSON))

        then: "Rebook should fail"
        rebookResult.andExpect(status().isConflict())
    }

    def "should correctly prevent overlapping bookings and blocks"() {
        given: "An available property exists for a given period"
        def initialStartAt = parse("2026-02-01")
        def initialEndAt = parse("2026-02-10")
        def availablePropertiesResult = mockMvc.perform(get("/api/properties")
                .param("startAt", initialStartAt.toString())
                .param("endAt", initialEndAt.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andReturn()
        def propertyId = objectMapper.readTree(availablePropertiesResult.getResponse().getContentAsString())[0].get("id").asText()

        when: "A booking is created for that property"
        def createBookingRequest = new CreateBookingRequest(UUID.fromString(propertyId), initialStartAt, initialEndAt, [new GuestDto("John", "john.smith@test.com")])
        def bookingResult = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isCreated())
                .andReturn()
        def bookingId = objectMapper.readTree(bookingResult.getResponse().getContentAsString()).get("id").asText()

        then: "Trying to create overlapping bookings should fail"
        def overlappingBookingStart = new CreateBookingRequest(UUID.fromString(propertyId), parse("2026-01-30"), parse("2026-02-05"), [new GuestDto("Fail", "fail1@test.com")])
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBookingStart)))
                .andExpect(status().isConflict())

        def overlappingBookingEnd = new CreateBookingRequest(UUID.fromString(propertyId), parse("2026-02-05"), parse("2026-02-15"), [new GuestDto("Fail", "fail2@test.com")])
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBookingEnd)))
                .andExpect(status().isConflict())

        def overlappingBookingInside = new CreateBookingRequest(UUID.fromString(propertyId), parse("2026-02-02"), parse("2026-02-09"), [new GuestDto("Fail", "fail3@test.com")])
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBookingInside)))
                .andExpect(status().isConflict())

        and: "Trying to create an overlapping block should also fail"
        def overlappingBlock = new CreateBlockRequest(UUID.fromString(propertyId), parse("2026-02-05"), parse("2026-02-15"), BlockReason.MAINTENANCE)
        mockMvc.perform(post("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingBlock)))
                .andExpect(status().isConflict())

        when: "A non-overlapping block is created after the booking"
        def nonOverlappingBlock = new CreateBlockRequest(UUID.fromString(propertyId), parse("2026-02-10"), parse("2026-02-20"), BlockReason.MAINTENANCE)
        mockMvc.perform(post("/api/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonOverlappingBlock)))
                .andExpect(status().isCreated())

        then: "Trying to update the original booking to overlap with the new block should fail"
        def updateRequest = new UpdateBookingRequest(parse("2026-02-01"), parse("2026-02-15"), [new GuestDto("Fail", "fail1@test.com")])
        mockMvc.perform(put("/api/bookings/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())

        when: "The original booking is cancelled"
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isNoContent())

        then: "The property becomes available again for the initial dates"
        mockMvc.perform(get("/api/properties")
                .param("startAt", initialStartAt.toString())
                .param("endAt", initialEndAt.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.[*].id', hasItem(propertyId)))
    }
}
