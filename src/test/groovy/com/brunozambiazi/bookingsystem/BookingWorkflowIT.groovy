package com.brunozambiazi.bookingsystem

import com.brunozambiazi.bookingsystem.api.dto.CreateBookingRequest
import com.brunozambiazi.bookingsystem.api.dto.GuestDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BookingWorkflowIT extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    def "should follow the complete booking workflow"() {
        given:
        def startAt = LocalDate.parse("2026-02-01")
        def endAt = LocalDate.parse("2026-02-10")

        when: "get available properties"
        def availablePropertiesResult = mockMvc.perform(get("/api/properties")
                .param("startAt", startAt.toString())
                .param("endAt", endAt.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.length()').value(1))
                .andReturn()

        def propertyId = objectMapper.readTree(availablePropertiesResult.getResponse().getContentAsString())[0].get("id").asText()

        and: "book for that period"
        def createBookingRequest = new CreateBookingRequest(UUID.fromString(propertyId), startAt, endAt, [new GuestDto("Jane", "jane.doe@test.com")])
        def bookingResult = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isCreated())
                .andReturn()

        def bookingId = objectMapper.readTree(bookingResult.getResponse().getContentAsString()).get("id").asText()

        then: "check if booking is ok"
        mockMvc.perform(get("/api/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.status').value("ACTIVE"))

        when: "cancel the booking"
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isNoContent())

        then: "booking is cancelled"
        mockMvc.perform(get("/api/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.status').value("CANCELLED"))

        when: "rebook the booking"
        mockMvc.perform(put("/api/bookings/" + bookingId + "/rebook"))
                .andExpect(status().isNoContent())

        then: "booking is confirmed again"
        mockMvc.perform(get("/api/bookings/" + bookingId))
                .andExpect(status().isOk())

        and: "get available properties for the same period and see none"
        mockMvc.perform(get("/api/properties")
                .param("startAt", startAt.toString())
                .param("endAt", endAt.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.length()').value(0))
    }
}
