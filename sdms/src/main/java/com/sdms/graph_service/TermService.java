package com.sdms.graph_service;

import com.alibaba.fastjson.JSONArray;
import com.sdms.graph_entity.TermEntity;
import org.springframework.stereotype.Service;

@Service
public interface TermService {

    void save(TermEntity t);

    void saveAll(Iterable<TermEntity> t);

    void deleteById(long id);

    void deleteAll();

    TermEntity getById(long id);

    Iterable<TermEntity> findAll();

    void createRelation(String term, String relation, String endNumber);

    void addByJsonFile(JSONArray o);

//    void reByJsonFile(JSONArray o);

}
