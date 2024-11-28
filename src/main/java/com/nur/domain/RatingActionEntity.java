package com.nur.domain;

import com.nur.domain.id.RatingActionId;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

@Data
@Entity
public class RatingActionEntity implements BaseEntity, Persistable<RatingActionId> {

    @EmbeddedId
    private RatingActionId id;

    private Integer oldCrg;
    private String oldCrrOutlook;
    private Integer newCrg;
    private String newCrrOutlook;
    private String rattingComment;
    private Float oldCrr;
    private Float newCrr;

    @Transient
    private boolean isNewRecord = true;


    @Override
    public RatingActionId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNewRecord;
    }

    @PostLoad
    @PrePersist
    void trackNotNew(){
        this.isNewRecord = false;
    }
}