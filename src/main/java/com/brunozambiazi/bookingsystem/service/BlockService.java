package com.brunozambiazi.bookingsystem.service;

import com.brunozambiazi.bookingsystem.api.dto.BlockResponse;
import com.brunozambiazi.bookingsystem.api.dto.CreateBlockRequest;
import com.brunozambiazi.bookingsystem.api.dto.UpdateBlockRequest;
import com.brunozambiazi.bookingsystem.domain.entity.BlockEntity;
import com.brunozambiazi.bookingsystem.domain.repository.BlockRepository;
import com.brunozambiazi.bookingsystem.service.mapper.BlockMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final AvailabilityService availabilityService;
    private final BlockMapper blockMapper;
    private final BlockRepository blockRepository;
    private final LockService lockService;
    private final PropertyService propertyService;

    @Transactional
    public BlockResponse createBlock(CreateBlockRequest request) {
        propertyService.checkActiveProperty(request.propertyId());

        BlockEntity block = blockMapper.toEntity(request);
        log.info("Creating block: [{}]", block);
        validateAndSave(block);

        return blockMapper.toResponse(block);
    }

    @Transactional
    public void deleteBlock(UUID blockId) {
        BlockEntity block = blockRepository.getById(blockId);
        log.info("Deleting block: [{}]", block);
        blockRepository.delete(block);
    }

    public BlockResponse getBlockById(UUID blockId) {
        BlockEntity block = blockRepository.getById(blockId);
        return blockMapper.toResponse(block);
    }

    @Transactional
    public BlockResponse updateBlock(UUID blockId, UpdateBlockRequest request) {
        BlockEntity block = blockRepository.getById(blockId);
        blockMapper.updateEntity(block, request);
        log.info("Updating block: [{}]", block);
        validateAndSave(block);

        return blockMapper.toResponse(block);
    }

    private void validateAndSave(BlockEntity block) {
        lockService.acquireLockFor(block);
        availabilityService.checkOverlap(block);

        log.info("Saving block: [{}]", block);
        blockRepository.save(block);
    }
}
