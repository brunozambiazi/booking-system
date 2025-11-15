package com.brunozambiazi.bookingsystem.api;

import com.brunozambiazi.bookingsystem.api.dto.PropertyResponse;
import com.brunozambiazi.bookingsystem.domain.model.DateRange;
import com.brunozambiazi.bookingsystem.service.PropertyService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    ResponseEntity<List<PropertyResponse>> getAvailableProperties(
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startAt,
            @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endAt
    ) {
        log.info("Received request to get properties between [{}] and [{}]", startAt, endAt);

        DateRange range = new DateRange(startAt, endAt);
        List<PropertyResponse> response = propertyService.findAvailableProperties(range);
        log.info("Get properties finished: [{}] found", response.size());

        return ok(response);
    }
}