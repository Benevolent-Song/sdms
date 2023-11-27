package com.sdms.graph_service.impl;

import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DomainRelationEntity;
import com.sdms.graph_service.DomainRelationService;
import com.sdms.repository.DomainRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DomainRelationServiceImpl implements DomainRelationService {

    @Autowired
    DomainRelationRepository domainRelationRepository;

    @Override
    public DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> save(DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> domainRelationEntity) {
        return domainRelationRepository.save(domainRelationEntity);
    }

    @Override
    public Iterable<DomainRelationEntity<BaseNodeEntity, BaseNodeEntity>> getAll() {
        return domainRelationRepository.findAll();
    }

    @Override
    public DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> getById(long id) {
        return domainRelationRepository.findById(id).get();
    }

    @Override
    public String delete(long id) {
        domainRelationRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    public void deleteAll() {
        domainRelationRepository.deleteAll();
    }
}
