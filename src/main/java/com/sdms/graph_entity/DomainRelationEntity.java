package com.sdms.graph_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "同领域")
public class DomainRelationEntity<S extends BaseNodeEntity, E extends BaseNodeEntity> extends BaseRelationEntity {

    @StartNode
    private S startNode;

    @EndNode
    private E endNode;
}
