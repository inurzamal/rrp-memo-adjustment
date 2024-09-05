package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
import com.nur.repository.RrpMemoERARepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class RrpMemoERAServiceImplTest {

    @Mock
    private RrpMemoERARepository repository;

    @InjectMocks
    private RrpMemoERAServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRrpMemos() {
        // Mocking repository response
        when(repository.getAllRrpMemos()).thenReturn(createEntityList());

        // Execution
        List<RrpMemoERADTO> result = service.getAllRrpMemoData();

        // Verification
        assertNotNull(result);
        assertEquals(2, result.size());
        //assertEquals("MLE123", result.get(0).getMleGlEntyId());
        verify(repository, times(1)).getAllRrpMemos();
    }

    @Test
    void testUploadRrpMemo() {
        // Mock repository save
        when(repository.saveAll(anyList())).thenReturn(createEntityList());

        // Execution
        service.uploadRrpMemo(createDtoList());

        // Verification
        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateRrpMemo() {
        // Mock data
        RrpMemoERADTO dto = createDto();
        RrpMemoERAEntity entity = createEntity();

        // Mock repository findById and save
        when(repository.findById(any(RrpMemoEntityId.class))).thenReturn(java.util.Optional.of(entity));
        when(repository.save(any(RrpMemoERAEntity.class))).thenReturn(entity);

        // Execution
        service.updateRrpMemo(dto);

        // Verification
        verify(repository, times(1)).findById(any(RrpMemoEntityId.class));
        verify(repository, times(1)).save(any(RrpMemoERAEntity.class));
    }

    @Test
    void testDeleteRrpMemo() {
        // Execution
        service.deleteRrpMemo(createDtoList());

        // Verification
        verify(repository, times(1)).deleteAllById(anyList());
        verify(repository, times(1)).flush();
    }

    // Generate DTO using Instancio
    private RrpMemoERADTO createDto() {
        return Instancio.of(RrpMemoERADTO.class)
                .set(field(RrpMemoERADTO::getMleGlEntyId), "MLE123")
                .set(field(RrpMemoERADTO::getClndrId), 2024)
                .set(field(RrpMemoERADTO::getIsNew), "Y")
                .create();
    }

    // Generate Entity using Instancio
    private RrpMemoERAEntity createEntity() {
        return Instancio.of(RrpMemoERAEntity.class)
                .set(field(RrpMemoERAEntity::getId), new RrpMemoEntityId("MLE123", 2024))
                .set(field(RrpMemoERAEntity::isActive), true)
                .create();
    }

    // Generate lists of DTOs and Entities
    private List<RrpMemoERADTO> createDtoList() {
        return Instancio.ofList(RrpMemoERADTO.class).size(2).create();
    }

    private List<RrpMemoERAEntity> createEntityList() {
        return Instancio.ofList(RrpMemoERAEntity.class).size(2).create();
    }
}
