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
@Document(indexName = "images")
public class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String title;

    private String number;

    private String chapter;

    private Integer page;//页数

    private Integer page2;//章节数

    private String name;

    private String path;

}
