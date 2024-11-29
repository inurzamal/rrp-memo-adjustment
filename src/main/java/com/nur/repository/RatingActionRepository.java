package com.nur.repository;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RatingActionRepository extends JpaRepository<RatingActionEntity, RatingActionId> {

    @Query(value = "SELECT * FROM rating_action_entity WHERE country = :country AND rating_date = :ratingDate", nativeQuery = true)
    Optional<RatingActionEntity> findByCountryAndRatingDate(@Param("country") String country, @Param("ratingDate") LocalDate ratingDate);

    @Query(value = "SELECT * FROM rating_action_entity", nativeQuery = true)
    List<RatingActionEntity> findAllData();
}
