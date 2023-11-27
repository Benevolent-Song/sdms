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
@NodeEntity("术语")
public class TermEntity extends BaseNodeEntity {

    @Property("术语名")
    private String term;

    @Property("术语英文名")
    private String termEn;

    @Property("描述")
    private String definition;

    @Property("来源")
    private String sourceNumber;

//    @Property("章节")
//    private String chapter;
//
//    @Property("页码")
//    private int page;

}
