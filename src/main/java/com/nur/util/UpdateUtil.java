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
    public static <T, D, ID> void updateEntity(
            JpaRepository<T, ID> repository, ID id, D updatedDTO) {

        // Fetch the existing entity by composite ID
        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));

        // Copy necessary fields from the updated DTO to the existing entity
        BeanUtils.copyProperties(updatedDTO, existingEntity, getNullPropertyNames(updatedDTO));

        // Save the updated entity
        repository.save(existingEntity);  // Hibernate will treat this as an update since it is attached to the persistence context
    }

    // Utility method to ignore null values while copying properties
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        return Arrays.stream(pds)
                .map(java.beans.PropertyDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}

