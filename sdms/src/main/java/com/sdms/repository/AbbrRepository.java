package com.sdms.repository;

import com.sdms.graph_entity.AbbrEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbbrRepository extends Neo4jRepository<AbbrEntity, Long> {

    @Query("match (n:`缩略语`{`来源`:{0}}),(m:`标准`{`编号`:{0}})" +
            "create (n)-[r:`来源于`{relation:{1}}]->(m)")
    void createSource(String startNumber, String relation);

    @Query("match (n:`缩略语`{`来源`:{0}}),(m:`缩略语`{`来源`:{2}})" +
            "create (n)-[r:`同来源`{relation:{1}}]->(m)")
    void createSameSource(String startNumber, String relation, String endNumber);

}
