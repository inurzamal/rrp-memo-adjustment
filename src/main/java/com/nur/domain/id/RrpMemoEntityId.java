package com.nur.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RrpMemoEntityId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "MLE_GL_ENTY_ID")
    private String mleGlEntyId;

    @Column(name = "CLNDR_ID")
    private Integer clndrId;




}
