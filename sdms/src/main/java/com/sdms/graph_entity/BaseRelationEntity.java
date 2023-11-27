package com.sdms.graph_entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import java.io.Serializable;

@Data
public class BaseRelationEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String relation;

}
