package com.sdms.graph_service.impl;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SourceRelationEntity;
import com.sdms.graph_service.SourceRelationService;
import com.sdms.repository.SourceRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceRelationServiceImpl implements SourceRelationService {

    @Autowired
    SourceRelationRepository sourceRelationRepository;

    @Override
    public SourceRelationEntity<BaseNodeEntity> save(SourceRelationEntity<BaseNodeEntity> d) {
        return sourceRelationRepository.save(d);
    }

    @Override
    public SourceRelationEntity<BaseNodeEntity> getById(long id) {
        return sourceRelationRepository.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        sourceRelationRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        sourceRelationRepository.deleteAll();
    }
}
