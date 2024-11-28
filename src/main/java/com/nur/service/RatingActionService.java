package com.nur.service;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;

import java.util.List;
import java.util.Optional;

public interface RatingActionService{

    public RatingActionEntity addRatingAction(RatingActionEntity action);

    public Optional<RatingActionEntity> getRatingActionById(RatingActionId id);

    public List<RatingActionEntity> getAllRatingActions();
}
