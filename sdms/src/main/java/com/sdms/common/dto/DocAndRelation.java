package com.sdms.common.dto;

import com.sdms.graph_entity.DocumentEntity;
import lombok.Data;

import java.util.List;

@Data
public class DocAndRelation {

    private List<DocumentEntity> nodes;

    private List<Relation> links;

    private List<Name> categories;

}
