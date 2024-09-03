package com.nur.repository;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RrpMemoERARepository extends JpaRepository<RrpMemoERAEntity, RrpMemoEntityId> {

    @Query("SELECT rrpMemo FROM RrpMemoERAEntity rrpMemo ORDER BY rrpMemo.modifiedTs DESC, rrpMemo.createdTs DESC")
    List<RrpMemoERAEntity> getAllRrpMemos();
}
