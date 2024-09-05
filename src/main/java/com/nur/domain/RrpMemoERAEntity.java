package com.nur.domain;

import com.nur.domain.id.RrpMemoEntityId;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ADJST_REF_RRP_MLE_MEMO")
public class RrpMemoERAEntity implements BaseEntity, Persistable<RrpMemoEntityId> {

    @EmbeddedId
    private RrpMemoEntityId id;

    @Column(name = "IS_ACTIVE")
    private boolean isActive;

    @Column(name = "IS_NEW")
    private String isNew;

    @Column(name = "BATCH_CD")
    private String batchCd;

    @Column(name = "MLE_ANNMNT_YEAR")
    private Integer mleAnnmntYear;

    @Column(name = "CREATED_TS")
    private LocalDateTime createdTs;

    @Column(name = "MODIFIED_TS")
    private LocalDateTime modifiedTs;

    @Transient
    private boolean isNewRecord = true;


    @Override
    public RrpMemoEntityId getId() {
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
