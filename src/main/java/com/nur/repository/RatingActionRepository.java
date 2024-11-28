package com.nur.repository;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingActionRepository extends JpaRepository<RatingActionEntity, RatingActionId> {
}
