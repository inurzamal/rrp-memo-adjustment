package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
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
        // Mocking repository response
        when(repository.getAllRrpMemos()).thenReturn(entityList);

        // Execution
        List<RrpMemoERADTO> result = service.getAllRrpMemoData();

        // Verification
        assertNotNull(result);
        assertEquals(entityList.size(), result.size());
        verify(repository, times(1)).getAllRrpMemos();
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testUploadRrpMemo(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {
        // Mock repository save
        when(repository.saveAll(anyList())).thenReturn(entityList);

        // Execution
        service.uploadRrpMemo(dtoList);

        // Verification
        verify(repository, times(1)).saveAll(anyList());
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testUpdateRrpMemo(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {
        RrpMemoERADTO dto = dtoList.get(0);
        RrpMemoERAEntity entity = entityList.get(0);

        // Mock repository findById and save
        when(repository.findById(any(RrpMemoEntityId.class))).thenReturn(Optional.of(entity));
        when(repository.save(any(RrpMemoERAEntity.class))).thenReturn(entity);

        // Execution
        service.updateRrpMemo(dto);

        // Verification
        verify(repository, times(1)).findById(any(RrpMemoEntityId.class));
        verify(repository, times(1)).save(any(RrpMemoERAEntity.class));
    }

    @Test
    @Disabled
    void testUpdateRrpMemoEntityNotFound() {
        RrpMemoERADTO dto = new RrpMemoERADTO();
        dto.setMleGlEntyId("MLE123");
        dto.setClndrId(2024);

        // Mock repository to return empty Optional
        when(repository.findById(any(RrpMemoEntityId.class))).thenReturn(Optional.empty());

        // Execution and Verification
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.updateRrpMemo(dto));
        assertEquals("Rrp Memo record not found with ID: " + new RrpMemoEntityId(dto.getMleGlEntyId(), dto.getClndrId()), exception.getMessage());
    }


    @ParameterizedTest
    @MethodSource("dataProvider")
    void testDeleteRrpMemo(List<RrpMemoERADTO> dtoList, List<RrpMemoERAEntity> entityList) {
        // Execution
        service.deleteRrpMemo(dtoList);

        // Verification
        verify(repository, times(1)).deleteAllById(anyList());
        verify(repository, times(1)).flush();
    }

    // Data provider method for parameterized tests using Arguments.of()
    private static Stream<Arguments> dataProvider() {
        RrpMemoERADTO dto1 = new RrpMemoERADTO();
        dto1.setMleGlEntyId("MLE123");
        dto1.setClndrId(2024);
        dto1.setIsNew("YES");

        RrpMemoERAEntity entity1 = new RrpMemoERAEntity();
        entity1.setId(new RrpMemoEntityId(dto1.getMleGlEntyId(), dto1.getClndrId()));
        entity1.setActive(true);

        RrpMemoERADTO dto2 = new RrpMemoERADTO();
        dto2.setMleGlEntyId("MLE124");
        dto2.setClndrId(2023);
        dto2.setIsNew("NO");

        RrpMemoERAEntity entity2 = new RrpMemoERAEntity();
        entity2.setId(new RrpMemoEntityId(dto2.getMleGlEntyId(), dto2.getClndrId()));
        entity2.setActive(false);

        // Returning a Stream<Arguments> using Arguments.of()
        return Stream.of(
                Arguments.of(Arrays.asList(dto1), Arrays.asList(entity1)),
                Arguments.of(Arrays.asList(dto2), Arrays.asList(entity2))
        );
    }
}



/*
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
*/
