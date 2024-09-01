package com.nur.service.impl;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import com.nur.dto.RrpMemoERADTO;
import com.nur.repository.RrpMemoERARepository;
import com.nur.service.RrpMemoERAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RrpMemoERAServiceImpl implements RrpMemoERAService {

    private final RrpMemoERARepository repository;

    @Override
    public void uploadRrpMemo(MultipartFile file) throws IOException {
        List<RrpMemoERADTO> dtos = parseExcelFile(file);
        List<RrpMemoERAEntity> entities = mapDtosToEntities(dtos);
        repository.saveAll(entities);
        log.info("Uploaded {} records from the Excel file.", entities.size());
    }

    private List<RrpMemoERADTO> parseExcelFile(MultipartFile file) throws IOException {
        List<RrpMemoERADTO> dtos = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (Row row : sheet) {
                // Skip the header row
                if (row.getRowNum() == 0) {
                    continue;
                }

                RrpMemoERADTO dto = new RrpMemoERADTO();
                dto.setActive(true);
                dto.setIsNew(row.getCell(0).getStringCellValue());
                dto.setMleGlEntyId(row.getCell(1).getStringCellValue());
                dto.setClndrId((int) row.getCell(2).getNumericCellValue());
                dto.setBatchCd(row.getCell(3).getStringCellValue());
                dto.setMleAnnmntYear((int) row.getCell(4).getNumericCellValue());
                dto.setUploadTime(row.getCell(5).getDateCellValue());
                dto.setModifiedTime(new Date());
                dtos.add(dto);
            }
        }
        return dtos;
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
            entity.setUploadTime(dto.getUploadTime());
            entity.setModifiedTime(dto.getModifiedTime());
            entities.add(entity);
        }
        return entities;
    }
}
