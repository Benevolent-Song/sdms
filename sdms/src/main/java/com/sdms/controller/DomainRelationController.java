package com.sdms.controller;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DomainRelationEntity;
import com.sdms.graph_service.DomainRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("relation")
public class DomainRelationController {

    @Autowired
    DomainRelationService domainRelationService;

    @PostMapping("/save")
    public DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> save(@RequestBody DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> domainRelationEntity) {

        return domainRelationService.save(domainRelationEntity);

    }

    @PostMapping("/getAll")
    public Iterable<DomainRelationEntity<BaseNodeEntity, BaseNodeEntity>> findAll() {

        return domainRelationService.getAll();

    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") long id) {
        return domainRelationService.delete(id);
    }

}
