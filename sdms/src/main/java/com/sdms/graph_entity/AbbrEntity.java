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
@NodeEntity("缩略语")
public class AbbrEntity extends BaseNodeEntity{

    @Property("缩略语")
    private String abbr;

    @Property("全称")
    private String full;

    @Property("描述")
    private String explain;

    @Property("来源")
    private String sourceNumber;

    @Property("章节")
    private String chapter;

    @Property("页码")
    private int page;

}
