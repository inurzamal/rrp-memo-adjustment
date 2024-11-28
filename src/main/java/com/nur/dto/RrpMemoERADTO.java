package com.nur.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RrpMemoERADTO implements BaseDTO, DTOEntity {

    private boolean isActive; // make it Boolean
    private String isNew;
    private String mleGlEntyId;
    private Integer clndrId;
    private String batchCd;
    private Integer mleAnnmntYear;
    private LocalDateTime createdTs;
    private LocalDateTime modifiedTs;

}
