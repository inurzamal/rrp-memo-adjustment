package com.nur.controller;

import com.nur.dto.RrpMemoERADTO;
import com.nur.service.RrpMemoERAService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/era/v1/adjustments/report")
public class RrpMemoERAController {

    @Autowired
    private RrpMemoERAService rrpMemoERAService;

//    @Value("${era.upload.rrpMemo.header.names}")
//    private String[] uploadHeaderNames;
//
//    @Value("${era.export.rrpMemo.header.names}")
//    private String[] exportHeaderNames;
//
//    @Value("${era.export.rrpMemo.field.names}")
//    private String[] exportFieldNames;

    @GetMapping("/rrpMemo")
    @Operation(summary = "This API is to fetch Rrp Memo Data")
    public List<RrpMemoERADTO> getRrpMemoData() {
        log.info("In getRrpMemo() era controller");
        return null;
    }

    @PostMapping(value = "/rrpMemo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "This API is to upload Rrp Memo Data")
    public void uploadRrpMemo(@RequestPart(required = true, name = "file") MultipartFile file) {
        log.info("This is uploadRrpMemo era controller");
        try {
            rrpMemoERAService.uploadRrpMemo(file);
            log.info("Upload successful.");
        } catch (IOException e) {
            log.error("Error occurred while uploading file: ", e);
            throw new RuntimeException("File upload failed, please try again.");
        }
    }

    @GetMapping("/rrpMemo/export")
    @Operation(summary = "This API is to export Rrp Memo Data")
    public ResponseEntity<InputStreamResource> exportRrpMemo(){
        log.info("This is exportRrpMemo era controller");

        return null;
    }

    @DeleteMapping("/rrpMemo/delete")
    @Operation(summary = "This API is to delete Rrp Memo Data")
    public void deleteRrpMemo(@RequestBody(required = true) List<RrpMemoERADTO> rrpMemoERADTOList){
        log.info("This is deleteRrpMemo era controller");

    }

    @PostMapping("/rrpMemo")
    @Operation(summary = "This API is to update Rrp Memo Data")
    public void updateRrpMemo(@RequestBody(required = true) RrpMemoERADTO rrpMemoERADTO) {
        log.info("This is updateRrpMemo era controller");

    }

}
