package com.nur.service.impl;

import com.nur.domain.RatingActionEntity;
import com.nur.domain.id.RatingActionId;
import com.nur.repository.RatingActionRepository;
import com.nur.service.RatingActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingActionServiceImpl implements RatingActionService {
    private final RatingActionRepository repository;

    @Autowired
    public RatingActionServiceImpl(RatingActionRepository repository) {
        this.repository = repository;
    }

    public RatingActionEntity addRatingAction(RatingActionEntity action) {
        return repository.save(action);
    }

    public Optional<RatingActionEntity> getRatingActionById(RatingActionId id) {
        return repository.findById(id);
    }

    @Override
    public List<RatingActionEntity> getAllRatingActions() {
        return repository.findAll();
    }
}

