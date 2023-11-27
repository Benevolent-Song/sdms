package com.sdms.graph_entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity("标准")
public class DocumentEntity extends BaseNodeEntity {

    @Property("中文标题")
    private String titleCn;

    @Property("英文标题")
    private String titleEn;

    @Property("类别")
    private String category;

    @Property("编号")
    private String number;

    @Property("编写单位")
    private String issuedBy;

    @Property("发布日期")
    private String releaseDate;

    @Property("实施日期")
    private String implementDate;

    @Property("领域")
    private String domain;

    @Override
    public String toString() {
        return "DocumentEntity{" +
                "id=" + super.getId() +
                ", titleCn='" + titleCn + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", category='" + category + '\'' +
                ", number='" + number + '\'' +
                ", issuedBy='" + issuedBy + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", implementDate='" + implementDate + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
