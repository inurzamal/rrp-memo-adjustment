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
        List<RrpMemoERAEntity> entities = mapDtosToEntities(dtos);
        repository.saveAll(entities);
        log.info("Uploaded {} records from the DTOs.", entities.size());
    }

    private List<RrpMemoERAEntity> mapDtosToEntities(List<RrpMemoERADTO> dtos) {
        List<RrpMemoERAEntity> entities = new ArrayList<>();
        for (RrpMemoERADTO dto : dtos) {
            RrpMemoERAEntity entity = new RrpMemoERAEntity();
            entity.setId(new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId()));
            entity.setActive(dto.isActive());
            entity.setIsNew(dto.getIsNew());
            entity.setBatchCd(dto.getBatchCd());
            entity.setMleAnnmntYear(dto.getMleAnnmntYear());
            entity.setCreatedTs(LocalDateTime.now());
            entity.setModifiedTs(LocalDateTime.now());
            entities.add(entity);
        }
        return entities;
    }
}
