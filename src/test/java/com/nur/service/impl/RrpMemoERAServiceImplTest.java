package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
import com.nur.exceptions.BadRequestException;
import com.nur.repository.RrpMemoERARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testGetAllRrpMemos(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {
        when(repository.getAllRrpMemos()).thenReturn(entityList);
        List<RrpMemoERADTO> result = service.getAllRrpMemoData();
        assertNotNull(result);
        assertEquals(entityList.size(), result.size());
        verify(repository, times(1)).getAllRrpMemos();
    }

//    @ParameterizedTest
//    @MethodSource("dataProvider")
//    void testUploadRrpMemo_withValidation(List<RrpMemoERADTO> validDtoList, List<RrpMemoERAEntity> entityList) {
//
//        when(repository.saveAll(anyList())).thenReturn(entityList);
//        service.uploadRrpMemo(validDtoList);
//        verify(repository, times(1)).saveAll(anyList());
//    }

    @Test
    void testUploadRrpMemo_updateExistingEntity() {
        // Mocking an existing entity in the repository
        RrpMemoEntityId entityId = new RrpMemoEntityId("MLE123", 2024);
        RrpMemoERAEntity existingEntity = new RrpMemoERAEntity();
        existingEntity.setId(new RrpMemoEntityId("MLE123",2024));


        // Creating a DTO with matching entityId
        RrpMemoERADTO validDto = new RrpMemoERADTO();
        validDto.setMleGlEntyId("MLE123");
        validDto.setClndrId(2024);
        validDto.setIsNew("Y");
        validDto.setBatchCd("B123");

        List<RrpMemoERADTO> validDtoList = Arrays.asList(validDto);

        // Mock repository behavior to return the existing entity
        when(repository.findById(entityId)).thenReturn(Optional.of(existingEntity));

        // Execution - should update the existing entity
        service.uploadRrpMemo(validDtoList);

        // Verify that the existing entity was updated and saved
        verify(repository, times(1)).saveAll(anyList());
        assertEquals("Y", existingEntity.getIsNew()); // Ensure the entity was updated
        assertEquals("B123", existingEntity.getBatchCd()); // Ensure batch code was updated
    }

    @Test
    void testUploadRrpMemo_createNewEntity() {
        // Creating a DTO for a new entity (non-existing entityId)
        RrpMemoERADTO validDto = new RrpMemoERADTO();
        validDto.setMleGlEntyId("MLE999"); // Non-existent entity ID
        validDto.setClndrId(2025);
        validDto.setIsNew("Y");
        validDto.setBatchCd("B999");

        List<RrpMemoERADTO> validDtoList = Arrays.asList(validDto);

        // Mock repository behavior to return null (i.e., no existing entity found)
        RrpMemoEntityId newEntityId = new RrpMemoEntityId("MLE999", 2025);
        when(repository.findById(newEntityId)).thenReturn(Optional.empty());

        // Execution - should create a new entity
        service.uploadRrpMemo(validDtoList);

        // Verify that a new entity was created and saved
        verify(repository, times(1)).saveAll(anyList());
    }



    @Test
    void testUploadRrpMemo_withValidationFailure() {
        // Create an invalid DTO list with null or empty fields
        RrpMemoERADTO invalidDto = new RrpMemoERADTO();
        invalidDto.setIsNew(""); // invalid - isNew is empty
        invalidDto.setMleGlEntyId(null); // invalid - mleGlEntyId is null
        invalidDto.setClndrId(2024);
        invalidDto.setBatchCd("B123");

        List<RrpMemoERADTO> invalidDtoList = Arrays.asList(invalidDto);

        // Execution and Verification - should throw BadRequestException
        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.uploadRrpMemo(invalidDtoList));
        assertEquals("Field 'isNew' should not be null or empty.", exception.getMessage());

        // Ensure the repository save is never called due to validation failure
        verify(repository, never()).saveAll(anyList());
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testUpdateRrpMemo(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {
        RrpMemoERADTO dto = dtoList.get(0);
        RrpMemoERAEntity entity = entityList.get(0);

        when(repository.findById(any(RrpMemoEntityId.class))).thenReturn(Optional.of(entity));
        when(repository.save(any(RrpMemoERAEntity.class))).thenReturn(entity);

        service.updateRrpMemo(dto);

        verify(repository, times(1)).findById(any(RrpMemoEntityId.class));
        verify(repository, times(1)).save(any(RrpMemoERAEntity.class));
    }

    @Test
    void testUpdateRrpMemoEntityNotFound() {
        RrpMemoERADTO dto = new RrpMemoERADTO();
        dto.setMleGlEntyId("MLE123");
        dto.setClndrId(2024);

        when(repository.findById(any(RrpMemoEntityId.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.updateRrpMemo(dto));
        assertEquals("Entity not found with ID: " + new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId()), exception.getMessage());
    }


    @ParameterizedTest
    @MethodSource("dataProvider")
    void testDeleteRrpMemo(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {

        service.deleteRrpMemo(dtoList);

        verify(repository, times(1)).deleteAllById(anyList());
        verify(repository, times(1)).flush();
    }

    private static Stream<Arguments> dataProvider() {
        RrpMemoERADTO dto1 = new RrpMemoERADTO();
        dto1.setMleGlEntyId("MLE123");
        dto1.setClndrId(2024);
        dto1.setIsNew("YES");
        dto1.setBatchCd("B123");

        RrpMemoERAEntity entity1 = new RrpMemoERAEntity();
        entity1.setId(new RrpMemoEntityId(dto1.getMleGlEntyId(), dto1.getClndrId()));
        entity1.setActive(true);

        RrpMemoERADTO dto2 = new RrpMemoERADTO();
        dto2.setMleGlEntyId("MLE124");
        dto2.setClndrId(2023);
        dto2.setIsNew("NO");
        dto2.setBatchCd("B123");

        RrpMemoERAEntity entity2 = new RrpMemoERAEntity();
        entity2.setId(new RrpMemoEntityId(dto2.getMleGlEntyId(), dto2.getClndrId()));
        entity2.setActive(false);

        return Stream.of(
                Arguments.of(Arrays.asList(dto1), Arrays.asList(entity1)),
                Arguments.of(Arrays.asList(dto2), Arrays.asList(entity2))
        );
    }
}
