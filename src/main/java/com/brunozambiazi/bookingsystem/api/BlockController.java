package com.brunozambiazi.bookingsystem.api;

import com.brunozambiazi.bookingsystem.api.dto.BlockResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBlockRequest;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBlockRequest;
import com.brunozambiazi.bookingsystem.service.BlockService;
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
@RequestMapping("/api/blocks")
class BlockController {

    private final BlockService blockService;

    @PostMapping
    ResponseEntity<BlockResponse> createBlock(@RequestBody @Valid CreateBlockRequest request) {
        log.info("Received request to create block: [{}]", request);

        BlockResponse response = blockService.createBlock(request);
        log.info("Create block finished: [{}]", response);

        return created(URI.create("/api/blocks/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<BlockResponse> getBlock(@PathVariable UUID id) {
        log.info("Received request to get block: [{}]", id);

        BlockResponse response = blockService.getBlockById(id);
        log.info("Get block finished: [{}]", response);

        return ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<BlockResponse> updateBlock(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBlockRequest request
    ) {
        log.info("Received request to update block [{}]: [{}]", id, request);

        BlockResponse response = blockService.updateBlock(id, request);
        log.info("Update block finished: [{}]", response);

        return ok(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBlock(@PathVariable UUID id) {
        log.info("Received request to delete block: [{}]", id);

        blockService.deleteBlock(id);
        log.info("Delete block finished");

        return noContent().build();
    }
}