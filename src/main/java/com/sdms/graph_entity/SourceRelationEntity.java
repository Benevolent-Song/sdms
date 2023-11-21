package com.sdms.graph_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RelationshipEntity(type = "来源于")
public class SourceRelationEntity<S extends BaseNodeEntity> extends BaseRelationEntity{

    @StartNode
    private S startNode;

    @EndNode
    private DocumentEntity endNode;
}
