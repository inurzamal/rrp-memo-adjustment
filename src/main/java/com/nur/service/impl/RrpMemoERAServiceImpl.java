package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
import com.nur.repository.RrpMemoERARepository;
import com.nur.service.RrpMemoERAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RrpMemoERAServiceImpl implements RrpMemoERAService {

    private final RrpMemoERARepository repository;

    // RrpMemoERAServiceImpl.java
    @Override
    public List<RrpMemoERADTO> getAllRrpMemoData() {

        List<RrpMemoERAEntity> entities = repository.getAllRrpMemos();

        // Convert entities to DTOs
        List<RrpMemoERADTO> dtos = new ArrayList<>();
        for (RrpMemoERAEntity entity : entities) {
            RrpMemoERADTO dto = new RrpMemoERADTO();

            // Set DTO fields based on entity values
            dto.setMleGlEntyId(entity.getId().getMleGlEntyId()); // Set from embedded ID field
            dto.setClndrId(entity.getId().getClndrId());         // Set from embedded ID field
            dto.setActive(entity.isActive());
            dto.setIsNew(entity.getIsNew());
            dto.setBatchCd(entity.getBatchCd());
            dto.setMleAnnmntYear(entity.getMleAnnmntYear());
            dto.setCreatedTs(entity.getCreatedTs());
            dto.setModifiedTs(entity.getModifiedTs());

            // Add DTO to the list
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void uploadRrpMemo(List<RrpMemoERADTO> dtos) {
        List<RrpMemoERAEntity> entities = new ArrayList<>();

        for (RrpMemoERADTO dto : dtos) {
            RrpMemoEntityId entityId = new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId());

            // Check if an entity with the given ID already exists in the database
            RrpMemoERAEntity existingEntity = repository.findById(entityId).orElse(null);

            if (existingEntity != null) {
                // Update existing entity's fields from the DTO
                existingEntity.setIsNew(dto.getIsNew());
                existingEntity.setBatchCd(dto.getBatchCd());
                existingEntity.setMleAnnmntYear(dto.getMleAnnmntYear());
                existingEntity.setActive(dto.isActive());
                existingEntity.setModifiedTs(LocalDateTime.now());  // Update modified timestamp

                log.info("Updating existing entity with ID: {}", entityId);
                entities.add(existingEntity);
            } else {
                // Create a new entity from the DTO
                RrpMemoERAEntity newEntity = mapDtoToEntity(dto);
                newEntity.setCreatedTs(LocalDateTime.now());   // Set created timestamp
                newEntity.setModifiedTs(LocalDateTime.now());  // Set modified timestamp

                log.info("Creating new entity with ID: {}", entityId);
                entities.add(newEntity);
            }
        }

        // Save or update entities in the repository
        repository.saveAll(entities);
        log.info("Uploaded or updated {} records from the DTOs.", entities.size());
    }

    @Override
    public void updateRrpMemo(RrpMemoERADTO dto) {
        // Extract the composite ID from the DTO
        RrpMemoEntityId entityId = new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId());

        // Fetch the existing entity from the database
        RrpMemoERAEntity existingEntity = repository.findById(entityId)
                .orElseThrow(() -> new RuntimeException("Rrp Memo record not found with ID: " + entityId));

        // Update the existing entity's fields with values from the DTO
        existingEntity.setIsNew(dto.getIsNew());
        existingEntity.setBatchCd(dto.getBatchCd());
        existingEntity.setMleAnnmntYear(dto.getMleAnnmntYear());
        existingEntity.setActive(dto.isActive());
        existingEntity.setModifiedTs(LocalDateTime.now());  // Update modified timestamp

        // Save the updated entity
        repository.save(existingEntity);
        log.info("Updated entity with ID: {}", entityId);
    }


    // Maps a single DTO to a new entity, without handling existing records
    private RrpMemoERAEntity mapDtoToEntity(RrpMemoERADTO dto) {
        RrpMemoERAEntity entity = new RrpMemoERAEntity();
        entity.setId(new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId()));
        entity.setActive(dto.isActive());
        entity.setIsNew(dto.getIsNew());
        entity.setBatchCd(dto.getBatchCd());
        entity.setMleAnnmntYear(dto.getMleAnnmntYear());
        // Additional fields can be mapped here as needed
        return entity;
    }


}
