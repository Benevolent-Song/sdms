package com.sdms.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.sdms.repository")
public class Neo4jConfiguration {

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .uri("bolt://localhost:7687")
                .credentials("neo4j", "88202093")
                .build();
    }

    @Bean
    public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration) {
        return new SessionFactory(configuration, "com.sdms.graph_entity");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory(configuration()));
    }


}
