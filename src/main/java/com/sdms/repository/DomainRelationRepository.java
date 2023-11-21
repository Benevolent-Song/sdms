package com.sdms.repository;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DomainRelationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainRelationRepository extends Neo4jRepository<DomainRelationEntity<BaseNodeEntity, BaseNodeEntity>, Long> {


}
