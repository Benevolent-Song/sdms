package com.sdms.graph_service;

import com.alibaba.fastjson.JSONArray;
import com.sdms.graph_entity.AbbrEntity;
import org.springframework.stereotype.Service;

@Service
public interface AbbrService {

    void save(AbbrEntity t);

    void saveAll(Iterable<AbbrEntity> t);

    void deleteById(long id);

    void deleteAll();

    AbbrEntity getById(long id);

    Iterable<AbbrEntity> findAll();

    void createRelation(String startNumber, String relation, String endNumber);

    void parseJsonFile(JSONArray o);
    
}
