package com.sdms.repository;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SameSourceRelation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SameSourceRelationRepository extends Neo4jRepository<SameSourceRelation<BaseNodeEntity, BaseNodeEntity>, Long> {
}
