package com.nur.service;

import com.nur.domain.RatingActionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RatingActionService{

    public RatingActionEntity addRatingAction(RatingActionEntity action);

    public Optional<RatingActionEntity> getRatingActionByCountryAndDate(String country, LocalDate ratingDate);

    public List<RatingActionEntity> getAllRatingActions();
}
