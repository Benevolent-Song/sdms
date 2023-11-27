package com.sdms.graph_service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdms.graph_entity.AbbrEntity;
import com.sdms.graph_service.AbbrService;
import com.sdms.repository.AbbrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

@Service
public class AbbrServiceImpl implements AbbrService {

    @Autowired
    AbbrRepository abbrRepository;

    @Override
    public void save(AbbrEntity t) {
        abbrRepository.save(t);
    }

    @Override
    public void saveAll(Iterable<AbbrEntity> t) {
        abbrRepository.saveAll(t);
    }

    @Override
    public void deleteById(long id) {
        abbrRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        abbrRepository.deleteAll();
    }

    @Override
    public AbbrEntity getById(long id) {
        return abbrRepository.findById(id).get();
    }

    @Override
    public Iterable<AbbrEntity> findAll() {
        return abbrRepository.findAll();
    }

    @Override
    public void createRelation(String startNumber, String relation, String endNumber) {
        if (relation.equals("来源于")) {
            abbrRepository.createSource(startNumber, relation);
        }

        if (relation.equals("同来源")) {
            abbrRepository.createSameSource(startNumber, relation, endNumber);
        }
    }

    @Override
    public void parseJsonFile(JSONArray o) {

        ArrayList<AbbrEntity> arrayList = new ArrayList<>();

        for (int i = 0; i < o.size(); i++) {

            JSONObject obj = o.getJSONObject(i);
            AbbrEntity AbbrEntity = new AbbrEntity();

            AbbrEntity.setAbbr(obj.getString("abbr"));
            AbbrEntity.setFull(obj.getString("full"));
            AbbrEntity.setExplain(obj.getString("explain"));
            AbbrEntity.setSourceNumber(obj.getString("number"));
            AbbrEntity.setChapter(obj.getString("chapter"));
            AbbrEntity.setPage(parseInt(obj.getString("page")));

            arrayList.add(AbbrEntity);

        }

        abbrRepository.saveAll(arrayList);

    }
}
