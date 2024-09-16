package com.nur.util;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateUtilTest {

    private JpaRepository<RrpMemoERAEntity, RrpMemoEntityId> repository;

    @BeforeEach
    void setUp() {
        repository = mock(JpaRepository.class);
    }

    @Test
    void testUpdateEntity_Success() {
        // Arrange
        RrpMemoEntityId id = new RrpMemoEntityId("123", 456);
        RrpMemoERAEntity existingEntity = new RrpMemoERAEntity();
        existingEntity.setId(id);
        existingEntity.setIsNew("OldValue");

        RrpMemoERAEntity updatedEntity = new RrpMemoERAEntity();
        updatedEntity.setId(id);
        updatedEntity.setIsNew("NewValue");

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(RrpMemoERAEntity.class))).thenReturn(existingEntity);

        // Act
        RrpMemoERAEntity result = UpdateUtil.updateEntity(repository, id, updatedEntity);

        // Assert
        verify(repository).findById(id);
        verify(repository).save(existingEntity);
        assertEquals("NewValue", existingEntity.getIsNew());
    }

    @Test
    void testUpdateEntity_EntityNotFound() {
        // Arrange
        RrpMemoEntityId id = new RrpMemoEntityId("123", 456);
        RrpMemoERAEntity updatedEntity = new RrpMemoERAEntity();
        updatedEntity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                UpdateUtil.updateEntity(repository, id, updatedEntity));
        assertEquals("Entity not found with ID: " + id, thrown.getMessage());
    }

    @Test
    void testGetNullPropertyNames() {
        // Arrange
        RrpMemoERAEntity entity = new RrpMemoERAEntity();
        entity.setIsNew("Test");

        // Act
        String[] nullPropertyNames = UpdateUtil.getNullPropertyNames(entity);

        // Assert
        assertTrue(Arrays.asList(nullPropertyNames).contains("batchCd"));
        assertTrue(Arrays.asList(nullPropertyNames).contains("mleAnnmntYear"));
        assertTrue(Arrays.asList(nullPropertyNames).contains("createdTs"));
        assertTrue(Arrays.asList(nullPropertyNames).contains("modifiedTs"));
        // Add checks for other properties that should be null
    }
}