package com.sdms.graph_service.impl;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SameSourceRelation;
import com.sdms.graph_service.SameSourceService;
import com.sdms.repository.SameSourceRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SameSourceServiceImpl implements SameSourceService {

    @Autowired
    SameSourceRelationRepository sameSourceRelationRepository;

    @Override
    public SameSourceRelation<BaseNodeEntity, BaseNodeEntity> save(SameSourceRelation<BaseNodeEntity, BaseNodeEntity> s) {
        return sameSourceRelationRepository.save(s);
    }

    @Override
    public SameSourceRelation<BaseNodeEntity, BaseNodeEntity> getById(long id) {
        return sameSourceRelationRepository.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        sameSourceRelationRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        sameSourceRelationRepository.deleteAll();
    }
}
