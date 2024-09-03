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

    private boolean isActive;
    private String isNew;
    private String batchCd;
    private Integer mleAnnmntYear;
    private LocalDateTime createdTs;
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
