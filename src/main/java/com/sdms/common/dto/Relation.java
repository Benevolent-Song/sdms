package com.sdms.common.dto;

import lombok.Data;

@Data
public class Relation {

    private Long id;

    private Long source;

    private Long target;

    private String name;

}
