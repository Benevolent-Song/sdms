package com.sdms.repository;

import com.sdms.graph_entity.DocumentEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface DocumentRepository extends Neo4jRepository<DocumentEntity, Long> {

    @Query("match(n:`标准`{`中文标题`:{titleCn}}) return n")
    DocumentEntity findByTitle(@Param("titleCn") String title);

    @Query("match(n:`标准`{`领域`:{category}}) return n")
    Collection<DocumentEntity> findByCategory(@Param("category") String category);

    @Query("match (n:`标准`{`编号`:{0}}),(m:`标准`{`编号`:{2}})" +
            "create (n)-[r:`同年份`{relation:{1}}]->(m)")
    void relationDate(String startNumber, String relation, String endNumber);

    @Query("match (n:`标准`{`编号`:{0}}),(m:标准{`编号`:{2}})" +
            "create (n)-[r:`同领域`{relation:{1}}]->(m)")
    void relationDomain(String startNumber, String relation, String endNumber);

    @Query("match (n:`标准`)-[r:`同领域`]-(m:`标准`) where n.`编号` = {number} return n,r,m limit 25")
    List<Map<String, Object>> queryNode(@Param("number") String number);

    @Query("match (n:`术语`)-[r:`来源于`]-(m:`标准`) where n.`术语名` = {term} return n,r,m limit 25")
    List<Map<String, Object>> queryProperty(@Param("term") String term);

}
