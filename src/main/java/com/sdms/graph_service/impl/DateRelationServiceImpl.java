package com.sdms.graph_service.impl;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DateRelationEntity;
import com.sdms.graph_service.DateRelationService;
import com.sdms.repository.DateRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DateRelationServiceImpl implements DateRelationService {

    @Autowired
    DateRelationRepository dateRelationRepository;

    @Override
    public DateRelationEntity<BaseNodeEntity, BaseNodeEntity> save(DateRelationEntity<BaseNodeEntity, BaseNodeEntity> d) {
        return dateRelationRepository.save(d);
    }

    @Override
    public DateRelationEntity<BaseNodeEntity, BaseNodeEntity> getById(long id) {
        return dateRelationRepository.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        dateRelationRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        dateRelationRepository.deleteAll();
    }
}
