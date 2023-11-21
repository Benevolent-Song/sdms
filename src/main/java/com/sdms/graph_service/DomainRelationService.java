package com.sdms.graph_service;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DomainRelationEntity;
import org.springframework.stereotype.Service;


@Service
public interface DomainRelationService {

    DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> save(DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> domainRelationEntity);

    Iterable<DomainRelationEntity<BaseNodeEntity, BaseNodeEntity>> getAll();

    DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> getById(long id);

    String delete(long id);

    void deleteAll();

}
