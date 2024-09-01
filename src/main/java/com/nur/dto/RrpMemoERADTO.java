package com.nur.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RrpMemoERADTO implements BaseDTO {

    private boolean isActive;
    private String isNew;
    private String mleGlEntyId;
    private Integer clndrId;
    private String batchCd;
    private Integer mleAnnmntYear;
    private Date uploadTime;
    private Date modifiedTime;

}
