package com.sdms.repository;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DateRelationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DateRelationRepository extends Neo4jRepository<DateRelationEntity<BaseNodeEntity, BaseNodeEntity>, Long> {
}
