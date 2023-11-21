package com.sdms.graph_service;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SameSourceRelation;
import org.springframework.stereotype.Service;

@Service
public interface SameSourceService {

    SameSourceRelation<BaseNodeEntity, BaseNodeEntity> save(SameSourceRelation<BaseNodeEntity, BaseNodeEntity> s);

    SameSourceRelation<BaseNodeEntity, BaseNodeEntity> getById(long id);

    void deleteById(long id);

    void deleteAll();

}
