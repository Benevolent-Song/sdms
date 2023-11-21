package com.sdms.graph_service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdms.graph_entity.TermEntity;
import com.sdms.graph_service.TermService;
import com.sdms.repository.TermRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class TermServiceImpl implements TermService {

    @Autowired
    TermRepository termRepository;

    @Override
    public void save(TermEntity t) {
        termRepository.save(t);
    }

    @Override
    public void saveAll(Iterable<TermEntity> t) {
        termRepository.saveAll(t);
    }

    @Override
    public void deleteById(long id) {
        termRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        termRepository.deleteAll();
    }

    @Override
    public TermEntity getById(long id) {
        return termRepository.findById(id).get();
    }

    @Override
    public Iterable<TermEntity> findAll() {
        return termRepository.findAll();
    }

    @Override
    public void createRelation(String term, String relation, String sourceNumber) {
        if (relation.equals("来源于")) {
            termRepository.createSource(term, relation, sourceNumber);
        }

    }

    @Override
    public void addByJsonFile(JSONArray o) {

        ArrayList<TermEntity> arrayList = new ArrayList<>();

        for (int i = 0; i < o.size(); i++) {

            JSONObject obj = o.getJSONObject(i);
            TermEntity termEntity = new TermEntity();

            termEntity.setTerm(obj.getString("term"));
            termEntity.setTermEn(obj.getString("termEn"));
            termEntity.setDefinition(obj.getString("definition"));
            termEntity.setSourceNumber(obj.getString("number"));
//            termEntity.setChapter(obj.getString("chapter"));
//            termEntity.setPage(parseInt(obj.getString("page")));

            arrayList.add(termEntity);

        }

        termRepository.saveAll(arrayList);

    }

//    @Override
//    public void reByJsonFile(JSONArray o) {
//
//        for (int i = 0; i < o.size(); i++) {
//
//            JSONObject obj = o.getJSONObject(i);
//
//            createRelation(obj.getString("term"), "来源于", obj.getString("number"));
//
//        }
//    }
}
