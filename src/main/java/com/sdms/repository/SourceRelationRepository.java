package com.sdms.repository;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.SourceRelationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRelationRepository extends Neo4jRepository<SourceRelationEntity<BaseNodeEntity>, Long> {
}
