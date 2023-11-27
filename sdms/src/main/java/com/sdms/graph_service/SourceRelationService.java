package com.sdms.graph_service;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SourceRelationEntity;
import org.springframework.stereotype.Service;

@Service
public interface SourceRelationService {

    SourceRelationEntity<BaseNodeEntity> save(SourceRelationEntity<BaseNodeEntity> d);

    SourceRelationEntity<BaseNodeEntity> getById(long id);

    void deleteById(long id);

    void deleteAll();

}
