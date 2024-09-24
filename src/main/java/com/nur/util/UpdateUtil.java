package com.nur.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@UtilityClass
public class UpdateUtil {

    @Transactional
    public static <T, D, ID> void updateData(
            JpaRepository<T, ID> repository, ID id, D updatedDTO, Class<T> entityClass) {

        // Fetch the existing entity by composite ID
        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));

        // Create a new entity instance and copy properties from the DTO
        T updatedEntity = BeanUtils.instantiateClass(entityClass);
        BeanUtils.copyProperties(updatedDTO, updatedEntity, getNullPropertyNames(updatedDTO));

        // Copy the necessary fields from the updated entity to the existing entity
        BeanUtils.copyProperties(updatedEntity, existingEntity, getNullPropertyNames(updatedEntity));

        // Save the updated entity
        repository.save(existingEntity);
    }

    // Utility method to ignore null values while copying properties
    String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        return Arrays.stream(pds)
                .map(java.beans.PropertyDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
