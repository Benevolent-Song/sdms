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
    private String id;

    private String pid;

    private String chapter;

    private String page;

    private String text;

}
