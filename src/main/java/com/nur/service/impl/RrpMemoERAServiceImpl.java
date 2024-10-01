package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
import com.nur.exceptions.BadRequestException;
import com.nur.repository.RrpMemoERARepository;
import com.nur.service.RrpMemoERAService;
import com.nur.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RrpMemoERAServiceImpl implements RrpMemoERAService {

    @Autowired
    private RrpMemoERARepository repository;

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

    @Transactional
    @Override
    public void uploadRrpMemo(List<RrpMemoERADTO> dtos) {
        // Directly validate DTOs; exception will be thrown if validation fails
        validateRrp(dtos);

        List<RrpMemoERAEntity> entities = new ArrayList<>();

        for (RrpMemoERADTO dto : dtos) {
            RrpMemoEntityId entityId = new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId());

            RrpMemoERAEntity existingEntity = repository.findById(entityId).orElse(null);

            if (existingEntity != null) {
                existingEntity.setIsNew(dto.getIsNew());
                existingEntity.setBatchCd(dto.getBatchCd());
                existingEntity.setMleAnnmntYear(dto.getMleAnnmntYear());
                existingEntity.setActive(false);
                existingEntity.setModifiedTs(LocalDateTime.now());

                log.info("Updating existing entity with ID: {}", entityId);
                entities.add(existingEntity);
            } else {
                RrpMemoERAEntity newEntity = mapDtoToEntity(dto);
                newEntity.setCreatedTs(LocalDateTime.now());
                newEntity.setModifiedTs(LocalDateTime.now());

                log.info("Creating new entity with ID: {}", entityId);
                entities.add(newEntity);
            }
        }

        repository.saveAll(entities);
        log.info("Uploaded or updated {} records from the DTOs.", entities.size());
    }



    // Validation method to check for null or empty fields
// Validation method to throw BadRequestException directly
    private void validateRrp(List<RrpMemoERADTO> dtos) {
        for (RrpMemoERADTO dto : dtos) {
            if (dto.getIsNew() == null || dto.getIsNew().isEmpty()) {
                throw new BadRequestException("Field 'isNew' should not be null or empty.");
            }
            if (dto.getMleGlEntyId() == null || dto.getMleGlEntyId().isEmpty()) {
                throw new BadRequestException("Field 'mleGlEntyId' should not be null or empty.");
            }
            if (dto.getClndrId() == null) {
                throw new BadRequestException("Field 'clndrId' should not be null.");
            }
            if (dto.getBatchCd() == null || dto.getBatchCd().isEmpty()) {
                throw new BadRequestException("Field 'batchCd' should not be null or empty.");
            }
        }
    }






/*    @Transactional
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
    }*/

    @Override
    public void updateRrpMemo(RrpMemoERADTO dto) {
        // Map DTO to updated entity
        RrpMemoERAEntity updatedEntity = new RrpMemoERAEntity();
        updatedEntity.setId(new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId()));
        updatedEntity.setIsNew(dto.getIsNew());
        updatedEntity.setBatchCd(dto.getBatchCd());
        updatedEntity.setMleAnnmntYear(dto.getMleAnnmntYear());
        updatedEntity.setActive(dto.isActive());
        updatedEntity.setModifiedTs(LocalDateTime.now());  // Set modified timestamp

        // Call the Update Utility with mapped entity
        UpdateUtil.updateEntity(
                repository, updatedEntity.getId(), updatedEntity
        );
    }

    @Transactional
    @Override
    public void deleteRrpMemo(List<RrpMemoERADTO> rrpMemoERADTOList) {
        log.info("In deleteRrpMemo() of Rrp Service impl");
        List<RrpMemoEntityId> entityIds = rrpMemoERADTOList.stream()
                .map(dto -> new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId())).toList();
        repository.deleteAllById(entityIds);
        repository.flush();
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
