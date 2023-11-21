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
@Document(indexName = "term")
public class Term implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String pid;

    private String chapter;

    private String page;

    private Integer page2;

    private String term;

    private String termEn;

    private String definition;

}
