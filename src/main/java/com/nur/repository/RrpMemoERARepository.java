package com.nur.repository;

import com.nur.domain.RrpMemoERAEntity;
import com.nur.domain.id.RrpMemoEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RrpMemoERARepository extends JpaRepository<RrpMemoERAEntity, RrpMemoEntityId> {
}
