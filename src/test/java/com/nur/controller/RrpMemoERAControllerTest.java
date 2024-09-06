package com.nur.controller;

import com.nur.dto.RrpMemoERADTO;
import com.nur.service.RrpMemoERAService;
import com.nur.util.ExcelGeneratorUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RrpMemoERAControllerTest {

    String[] exportHeaderNames = {"IS_ACTIVE", "IS_NEW", "MLE_GL_ENTY_ID", "CLNDR_ID"};
    String[] exportFieldNames = {"isActive", "isNew", "mleGlEntyId", "clndrId"};

    private static final String RRP_MEMO_SHEET_NAME = "rrp";

    @Mock
    private RrpMemoERAService rrpMemoERAService;

    @Mock
    private ExcelGeneratorUtil excelGeneratorUtil;

    @InjectMocks
    private RrpMemoERAController controller;

    @BeforeEach
    void setUp() {
        // Set the fields manually that were originally set with @Value annotations
        controller.exportHeaderNames = exportHeaderNames;
        controller.exportFieldNames = exportFieldNames;
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("provideRrpMemoData")
    void testGetRrpMemoData(List<RrpMemoERADTO> dtos) {
        when(rrpMemoERAService.getAllRrpMemoData()).thenReturn(dtos);
        List<RrpMemoERADTO> result = controller.getRrpMemoData();
        assertNotNull(result);
        assertEquals(dtos.size(), result.size());
        verify(rrpMemoERAService, times(1)).getAllRrpMemoData();
    }

    @ParameterizedTest
    @MethodSource("provideUploadData")
    void testUploadRrpMemo(MultipartFile file, List<RrpMemoERADTO> dtos) throws IOException {
        // Mock service call
        doNothing().when(rrpMemoERAService).uploadRrpMemo(anyList());

        // Execute
        controller.uploadRrpMemo(file);

        // Verify
        verify(rrpMemoERAService, times(1)).uploadRrpMemo(anyList());
    }

    @ParameterizedTest
    @MethodSource("provideRrpMemoData")
    void testExportRrpMemo(List<RrpMemoERADTO> dtos) {
        // Mock the static method ExcelGeneratorUtil.exportToExcel
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        try (MockedStatic<ExcelGeneratorUtil> mockedExcelUtil = mockStatic(ExcelGeneratorUtil.class)) {
            // Mocking the exportToExcel behavior
            mockedExcelUtil.when(() -> ExcelGeneratorUtil.exportToExcel(
                    eq(RRP_MEMO_SHEET_NAME),
                    eq(exportHeaderNames),
                    anyList(),
                    eq(exportFieldNames),
                    eq(21))
            ).thenReturn(byteArrayInputStream);

            // Mock service call
            when(rrpMemoERAService.getAllRrpMemoData()).thenReturn(dtos);

            // Execute
            ResponseEntity<InputStreamResource> response = controller.exportRrpMemo();

            // Verify response
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response.getHeaders().getContentType().toString());

            // Verify the mock interactions
            verify(rrpMemoERAService, times(1)).getAllRrpMemoData();
            mockedExcelUtil.verify(() -> ExcelGeneratorUtil.exportToExcel(
                    RRP_MEMO_SHEET_NAME,
                    exportHeaderNames,
                    dtos,
                    exportFieldNames,
                    21
            ), times(1));
        }
    }

    @ParameterizedTest
    @MethodSource("provideRrpMemoData")
    void testDeleteRrpMemo(List<RrpMemoERADTO> dtoList) {
        // Mocking the delete service
        doNothing().when(rrpMemoERAService).deleteRrpMemo(anyList());

        // Execute
        controller.deleteRrpMemo(dtoList);

        // Verify
        verify(rrpMemoERAService, times(1)).deleteRrpMemo(anyList());
    }

    @ParameterizedTest
    @MethodSource("provideRrpMemoData")
    void testUpdateRrpMemo(List<RrpMemoERADTO> dtoList) {
        RrpMemoERADTO dto = dtoList.get(0);

        // Mock service update call
        doNothing().when(rrpMemoERAService).updateRrpMemo(any(RrpMemoERADTO.class));

        // Execute
        controller.updateRrpMemo(dto);

        // Verify
        verify(rrpMemoERAService, times(1)).updateRrpMemo(any(RrpMemoERADTO.class));
    }

    // Edge Case Tests and Error Handling

    @Test
    void testUploadRrpMemoWithEmptyFile() throws IOException {
        // Create an empty MockMultipartFile
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{}
        );

        // Execute and Verify
        RuntimeException exception = assertThrows(RuntimeException.class, () -> controller.uploadRrpMemo(emptyFile));
        assertEquals("File upload failed, please try again.", exception.getMessage());
    }



    @Test
    void testExportRrpMemoThrowsException() {
        // Mock the static method ExcelGeneratorUtil.exportToExcel
        try (MockedStatic<ExcelGeneratorUtil> mockedExcelUtil = mockStatic(ExcelGeneratorUtil.class)) {
            // Mocking the exportToExcel behavior to throw an exception
            mockedExcelUtil.when(() -> ExcelGeneratorUtil.exportToExcel(
                    eq(RRP_MEMO_SHEET_NAME),
                    eq(exportHeaderNames),
                    anyList(),
                    eq(exportFieldNames),
                    eq(21))
            ).thenThrow(new RuntimeException("Excel export failed"));

            // Mock service call
            when(rrpMemoERAService.getAllRrpMemoData()).thenReturn(Collections.emptyList());

            // Execute
            ResponseEntity<InputStreamResource> response = controller.exportRrpMemo();

            // Verify response
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }
    }


    @Test
    void testDeleteRrpMemoWithEmptyList() {
        List<RrpMemoERADTO> emptyList = Collections.emptyList();

        // Call the delete method with an empty list
        controller.deleteRrpMemo(emptyList);

        // Verify that deleteRrpMemo was not called with an empty list
        verify(rrpMemoERAService, never()).deleteRrpMemo(anyList());
    }



    @Test
    void testUpdateRrpMemoThrowsException() {
        RrpMemoERADTO dto = new RrpMemoERADTO();

        // Mock service update call to throw an exception
        doThrow(new RuntimeException("Update failed")).when(rrpMemoERAService).updateRrpMemo(any(RrpMemoERADTO.class));

        // Execute and Verify
        Exception exception = assertThrows(RuntimeException.class, () -> controller.updateRrpMemo(dto));
        assertEquals("Update failed", exception.getMessage());
    }

    // Data Providers
    private static Stream<List<RrpMemoERADTO>> provideRrpMemoData() {
        RrpMemoERADTO dto1 = new RrpMemoERADTO();
        dto1.setMleGlEntyId("MLE123");
        dto1.setClndrId(2024);
        dto1.setIsNew("YES");

        RrpMemoERADTO dto2 = new RrpMemoERADTO();
        dto2.setMleGlEntyId("MLE124");
        dto2.setClndrId(2023);
        dto2.setIsNew("NO");

        return Stream.of(
                Collections.singletonList(dto1),
                Collections.singletonList(dto2)
        );
    }

    private static Stream<Arguments> provideUploadData() throws IOException {
        // Create a simple Excel file with Apache POI
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Sheet1");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Create a MockMultipartFile with valid Excel content
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new ByteArrayInputStream(outputStream.toByteArray())
        );

        RrpMemoERADTO dto1 = new RrpMemoERADTO();
        dto1.setMleGlEntyId("MLE123");
        dto1.setClndrId(2024);
        dto1.setIsNew("YES");

        return Stream.of(
                Arguments.of(file, List.of(dto1))
        );
    }
}
