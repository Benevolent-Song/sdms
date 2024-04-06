package com.sdms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "sdms", type = "_doc")//实体将被映射到名为 "sdms" 的Elasticsearch索引，并且在该索引中的文档将标记为类型 "_doc"。
public class EsDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;//段落的id,是由pid+n确定的(例如:pid+1表示是pid标号文章的第一段),id是唯一的

    private String pid;//是整篇文章的标识id,同一文章有相同id

    private String chapter;//段落的名称

    private String page;//在文本中的页数

    private String text;//段落的内容

}
