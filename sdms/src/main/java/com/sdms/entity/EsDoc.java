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
@Document(indexName = "sdms", type = "_doc")
public class EsDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String pid;

    private String chapter;

    private String page;

    private String text;

}
