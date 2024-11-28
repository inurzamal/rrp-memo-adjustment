package com.nur.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RatingDTO implements BaseDTO, DTOEntity {

    private String country;
    private LocalDate ratingDate;
    private Integer oldCrg;
    private String oldCrrOutlook;
    private Integer newCrg;
    private String newCrrOutlook;
    private String rattingComment;
    private Float oldCrr;
    private Float newCrr;
}
