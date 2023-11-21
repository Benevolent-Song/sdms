package com.sdms.repository;

import com.sdms.graph_entity.TermEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends Neo4jRepository<TermEntity, Long> {

    @Query("match (n:`术语`{`术语名`:{0},`来源`:{source}}),(m:`标准`{`编号`:{source}})" +
            "create (n)-[r:`来源于`{relation:{1}}]->(m)")
    void createSource(String term, String relation,@Param("source") String sourceNumber);

    @Query("match (n:`术语`{`id`:{0}}),(m:`术语`{`来源`:{2}})" +
            "create (n)-[r:`同来源`{relation:{1}}]->(m)")
    void createSameSource(Long id, String relation, String endNumber);

}
