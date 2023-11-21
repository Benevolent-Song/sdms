package com.sdms.graph_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NodeEntity("章节")
public class ChapterEntity extends BaseNodeEntity{

    @Property("章节")
    private String chapter;

    @Property("来源")
    private String sourceNumber;

    @Property("页码")
    private int page;

}
