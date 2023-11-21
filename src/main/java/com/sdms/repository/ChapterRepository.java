package com.sdms.repository;

import com.sdms.graph_entity.ChapterEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ChapterRepository extends Neo4jRepository<ChapterEntity, Long> {

    @Query("match (n:`术语`{`术语名`:{0}}),(m:`标准`{`编号`:{2}})" +
            "create (n)-[r:`来源于`{relation:{1}}]->(m)")
    void createSource(String term, String relation, String sourceNumber);


}
