package com.nur.controller;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;
import com.nur.dto.RatingDTO;
import com.nur.service.impl.RatingActionServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rating-actions")
@CrossOrigin
public class RatingActionController {

    private final RatingActionServiceImpl service;
    private final ModelMapper modelMapper;

    @Autowired
    public RatingActionController(RatingActionServiceImpl service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<RatingDTO> addRatingAction(@RequestBody RatingDTO dto) {
        RatingActionEntity action = modelMapper.map(dto, RatingActionEntity.class);
        action.setId(new RatingActionId(dto.getCountry(),dto.getRatingDate()));
        RatingActionEntity savedAction = service.addRatingAction(action);
        RatingDTO responseDto = modelMapper.map(savedAction, RatingDTO.class);
        responseDto.setCountry(dto.getCountry());
        responseDto.setRatingDate(dto.getRatingDate());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{country}/{ratingDate}")
    public ResponseEntity<RatingDTO> getRatingAction(
            @PathVariable String country,
            @PathVariable String ratingDate) {

        LocalDate parsedDate = LocalDate.parse(ratingDate);
        Optional<RatingActionEntity> action = service.getRatingActionByCountryAndDate(country, parsedDate);
        return action.map(value -> {
            RatingDTO dto = modelMapper.map(value, RatingDTO.class);
            if (value.getId() != null) {
                dto.setCountry(value.getId().getCountry());
                dto.setRatingDate(value.getId().getRatingDate());
            }
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/fetchAll")
    public ResponseEntity<List<RatingDTO>> getRatingActions() {
        List<RatingActionEntity> actions = service.getAllRatingActions();
        List<RatingDTO> dtoList = actions.stream().map(action -> {
            RatingDTO dto = modelMapper.map(action, RatingDTO.class);
            if (action.getId() != null) {
                dto.setCountry(action.getId().getCountry());
                dto.setRatingDate(action.getId().getRatingDate());
            }
            return dto;
        }).toList();
        return ResponseEntity.ok(dtoList);
    }

}
