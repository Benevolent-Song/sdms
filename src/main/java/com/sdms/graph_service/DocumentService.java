package com.sdms.graph_service;

import com.alibaba.fastjson.JSONArray;
import com.sdms.common.dto.DocAndRelation;
import com.sdms.graph_entity.DocumentEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public interface DocumentService {

    /**
     *  获取所有数据
     */
    Iterable<DocumentEntity> getAll();

    /**
     * 添加节点
     */
    int saveDocument(DocumentEntity document);

    DocumentEntity findByTile(String title);

    Collection<DocumentEntity> findByCategory(String category);

    long getCount();

    void deleteById(long id);

    void deleteAll();

    boolean existsById(long id);

    DocumentEntity findById(long id);

    void createRelation(String startNumber, String relation, String endNumber);

    void parseJsonFile(JSONArray o);

    DocAndRelation queryNode(String number);

    List<Map<String, Object>> queryProperty(String term);

}
