package com.nur.configuration;

import com.nur.domain.RatingActionEntity;
import com.nur.dto.RatingDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Custom mapping for RatingActionEntity to RatingDTO
        mapper.addMappings(new PropertyMap<RatingActionEntity, RatingDTO>() {
            @Override
            protected void configure() {
                map().setCountry(source.getId().getCountry());
                map().setRatingDate(source.getId().getRatingDate());
            }
        });

        return mapper;
    }
}

