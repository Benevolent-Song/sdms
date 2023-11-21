package com.sdms.graph_entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import java.io.Serializable;

@Data
public abstract class BaseNodeEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

}
