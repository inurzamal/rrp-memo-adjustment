package com.nur.controller;

import com.nur.dto.RrpMemoERADTO;
import com.nur.service.RrpMemoERAService;
import com.nur.util.CommonUtil;
import com.nur.util.ExcelGeneratorUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nur.util.CommonUtil.*;


@Slf4j
@RestController
@RequestMapping(value = "/api/era/v1/adjustments/report")
public class RrpMemoERAController {

    private static final String RRP_MEMO_FILE_NAME = "rrp";
    @Autowired
    private RrpMemoERAService rrpMemoERAService;

    @Value("${era.upload.rrpMemo.header.names}")
    private String[] uploadHeaderNames;

    @Value("${era.upload.rrpMemo.header.types}")
    private String[] uploadHeaderTypes;

    @Value("${era.export.rrpMemo.header.names}")
    private String[] exportHeaderNames;

    @Value("${era.export.rrpMemo.field.names}")
    private String[] exportFieldNames;

    @GetMapping("/rrpMemo")
    @Operation(summary = "This API is to fetch Rrp Memo Data")
    public List<RrpMemoERADTO> getRrpMemoData() {
        log.info("In getRrpMemo() era controller");
        return rrpMemoERAService.getAllRrpMemoData();
    }

    @PostMapping(value = "/rrpMemo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "This API is to upload Rrp Memo Data")
    public void uploadRrpMemo(@RequestPart(required = true, name = "file") MultipartFile file) {
        log.info("This is uploadRrpMemo era controller");
        try {
            // Parse Excel file into DTOs
            List<RrpMemoERADTO> dtos = parseExcelFile(file);

            // Pass DTOs to the service for mapping and saving
            rrpMemoERAService.uploadRrpMemo(dtos);
            log.info("Upload successful with {} records.", dtos.size());
        } catch (IOException e) {
            log.error("Error occurred while uploading file: ", e);
            throw new RuntimeException("File upload failed, please try again.");
        }
    }

    private List<RrpMemoERADTO> parseExcelFile(MultipartFile file) throws IOException {
        List<RrpMemoERADTO> dtos = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (Row row : sheet) {
                // Skip the header row and rows with missing/empty critical data
                if (row.getRowNum() == 0 || row.getCell(0) == null ||
                        CommonUtil.isAnyTrue(
                                isEmpty(getStringCellValue(row, 1, uploadHeaderNames, uploadHeaderTypes)),
                                isEmpty(getNumericCellValue(row, 2, uploadHeaderNames, uploadHeaderTypes)))) {
                    continue;
                }

                int col = 0;
                RrpMemoERADTO dto = new RrpMemoERADTO();
                dto.setActive(true);
                dto.setIsNew(getStringCellValue(row, col++, uploadHeaderNames, uploadHeaderTypes));
                dto.setMleGlEntyId(getStringCellValue(row, col++, uploadHeaderNames, uploadHeaderTypes));
                dto.setClndrId(getNumericCellValue(row, col++, uploadHeaderNames, uploadHeaderTypes));
                dto.setBatchCd(getStringCellValue(row, col++, uploadHeaderNames, uploadHeaderTypes));
                dto.setMleAnnmntYear(getNumericCellValue(row, col++, uploadHeaderNames, uploadHeaderTypes));
                dtos.add(dto);
            }
        } catch (IOException ex) {
            log.error("Error parsing Excel file", ex);
            throw new RuntimeException("Failed to parse Excel file.");
        }
        return dtos;
    }

    @GetMapping("/rrpMemo/export")
    @Operation(summary = "This API is to export Rrp Memo Data")
    public ResponseEntity<InputStreamResource> exportRrpMemo() {
        log.info("This is exportRrpMemo era controller");
        ByteArrayInputStream inputStream = null;

        try {
            // Generating Excel data
            inputStream = ExcelGeneratorUtil.exportToExcel(RRP_MEMO_FILE_NAME, exportHeaderNames, rrpMemoERAService.getAllRrpMemoData(), exportFieldNames,
                    "A1:V", // Adjust this range if needed
                    21      // Maximum column width
            );

            // Setting up response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RRP_MEMO_FILE_NAME + ".xlsx");

            return ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("Error occurred during Excel export: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }


    @DeleteMapping("/rrpMemo/delete")
    @Operation(summary = "This API is to delete Rrp Memo Data")
    public void deleteRrpMemo(@RequestBody(required = true) List<RrpMemoERADTO> rrpMemoERADTOList){
        log.info("This is deleteRrpMemo era controller");
        if (!CollectionUtils.isEmpty(rrpMemoERADTOList)) {
            rrpMemoERAService.deleteRrpMemo(rrpMemoERADTOList);
        }
    }

    @PutMapping("/rrpMemo")
    @Operation(summary = "This API is to update Rrp Memo Data")
    public void updateRrpMemo(@RequestBody(required = true) RrpMemoERADTO rrpMemoERADTO) {
        log.info("This is updateRrpMemo era controller");
        rrpMemoERAService.updateRrpMemo(rrpMemoERADTO);
    }
}
