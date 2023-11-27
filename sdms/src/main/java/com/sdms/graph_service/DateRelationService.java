package com.sdms.graph_service;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DateRelationEntity;
import org.springframework.stereotype.Service;

@Service
public interface DateRelationService {
    
    DateRelationEntity<BaseNodeEntity, BaseNodeEntity> save(DateRelationEntity<BaseNodeEntity, BaseNodeEntity> d);

    DateRelationEntity<BaseNodeEntity, BaseNodeEntity> getById(long id);

    void deleteById(long id);

    void deleteAll();
    
}
