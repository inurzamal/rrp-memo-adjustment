package com.nur.controller;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;
import com.nur.dto.RatingDTO;
import com.nur.service.impl.RatingActionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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

    @PutMapping
    public ResponseEntity<RatingDTO> updateRatingAction(@RequestBody RatingDTO dto) {

        Optional<RatingActionEntity> existingAction = service.getRatingActionByCountryAndDate(dto.getCountry(), dto.getRatingDate());

        if (existingAction.isPresent()) {
            RatingActionEntity action = existingAction.get();

            // Update the fields
            action.setOldCrg(dto.getOldCrg());
            action.setOldCrrOutlook(dto.getOldCrrOutlook());
            action.setNewCrg(dto.getNewCrg());
            action.setNewCrrOutlook(dto.getNewCrrOutlook());
            action.setRattingComment(dto.getRattingComment());
            action.setOldCrr(dto.getOldCrr());
            action.setNewCrr(dto.getNewCrr());

            // Save the updated entity
            RatingActionEntity updatedAction = service.addRatingAction(action);

            // Convert to DTO for response
            RatingDTO responseDto = modelMapper.map(updatedAction, RatingDTO.class);
            responseDto.setCountry(dto.getCountry());
            responseDto.setRatingDate(dto.getRatingDate());

            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // or add a custom error response if needed
        }
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an Excel file for rating actions")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid Excel file.");
        }

        try {
            // Read and parse the Excel file
            List<RatingDTO> dtoList = parseExcelFile(file);

            // Map and save the data
            for (RatingDTO dto : dtoList) {
                RatingActionEntity action = modelMapper.map(dto, RatingActionEntity.class);
                action.setId(new RatingActionId(dto.getCountry(), dto.getRatingDate()));
                service.addRatingAction(action);
            }

            return ResponseEntity.ok("File uploaded and data saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while processing the file: " + e.getMessage());
        }
    }

    private List<RatingDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<RatingDTO> dtoList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assumes data is in the first sheet

            // Iterate over rows, skipping the header
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // Skip header row

            while (rows.hasNext()) {
                Row row = rows.next();
                RatingDTO dto = new RatingDTO();

                dto.setCountry(row.getCell(0).getStringCellValue());
                dto.setRatingDate(row.getCell(1).getLocalDateTimeCellValue().toLocalDate());
                dto.setOldCrg((int) row.getCell(2).getNumericCellValue());
                dto.setOldCrrOutlook(row.getCell(3).getStringCellValue());
                dto.setNewCrg((int) row.getCell(4).getNumericCellValue());
                dto.setNewCrrOutlook(row.getCell(5).getStringCellValue());
                dto.setRattingComment(row.getCell(6).getStringCellValue());
                dto.setOldCrr((float) row.getCell(7).getNumericCellValue());
                dto.setNewCrr((float) row.getCell(8).getNumericCellValue());

                dtoList.add(dto);
            }
        }

        return dtoList;
    }

}
