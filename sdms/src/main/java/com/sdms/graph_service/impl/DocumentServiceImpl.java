package com.sdms.graph_service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdms.common.dto.DocAndRelation;
import com.sdms.common.dto.Name;
import com.sdms.common.dto.Relation;
import com.sdms.graph_entity.BaseNodeEntity;
import com.sdms.graph_entity.DocumentEntity;
import com.sdms.graph_entity.DomainRelationEntity;
import com.sdms.graph_service.DocumentService;
import com.sdms.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    /**
     * 获取所有数据
     *
     */
    @Override
    public Iterable<DocumentEntity> getAll() {

        return documentRepository.findAll();

    }

    /**
     * 添加节点
     */
    @Override
    public int saveDocument(DocumentEntity document) {

        DocumentEntity save = documentRepository.save(document);

        return 200;

    }

    @Override
    public DocumentEntity findByTile(String title) {

        return documentRepository.findByTitle(title);

    }

    @Override
    public Collection<DocumentEntity> findByCategory(String category) {

        return documentRepository.findByCategory(category);

    }

    @Override
    public long getCount() {
        return documentRepository.count();
    }

    @Override
    public void deleteById(long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        documentRepository.deleteAll();
    }

    @Override
    public boolean existsById(long id) {
        return documentRepository.existsById(id);
    }

    @Override
    public DocumentEntity findById(long id) {
        return documentRepository.findById(id).get();
    }

    @Override
    public void createRelation(String startNumber, String relation, String endNumber) {
        if(relation.equals("同领域")) {
            documentRepository.relationDomain(startNumber, relation, endNumber);
        }
        if(relation.equals("同年份")) {
            documentRepository.relationDate(startNumber, relation, endNumber);
        }
        System.out.println(startNumber + " -> " + endNumber);
    }

    @Override
    public void parseJsonFile(JSONArray o) {

        ArrayList<DocumentEntity> arrayList = new ArrayList<>();

        for (int i = 0; i < o.size(); i++) {

            JSONObject obj = o.getJSONObject(i);
            DocumentEntity documentEntity = new DocumentEntity();

            documentEntity.setCategory(obj.getString("category"));
            documentEntity.setDomain(obj.getString("domain"));
            documentEntity.setNumber(obj.getString("number"));
            documentEntity.setIssuedBy(obj.getString("issuedBy"));
            documentEntity.setImplementDate(obj.getString("implementDate"));
            documentEntity.setReleaseDate(obj.getString("releaseDate"));
            documentEntity.setTitleCn(obj.getString("titleCn"));
            documentEntity.setTitleEn(obj.getString("titleEn"));

            arrayList.add(documentEntity);

        }

        documentRepository.saveAll(arrayList);

    }

    @Override
    public DocAndRelation queryNode(String number) {

        List<Map<String, Object>> results = documentRepository.queryNode(number);
        DocAndRelation docAndRelation = new DocAndRelation();
        List<Relation> links = new ArrayList<>();
        Set<DocumentEntity> set = new HashSet<>();
        Set<Name> cate = new HashSet<>();

        for (Map<String, Object> row : results) {

            Relation relation = new Relation();

            DomainRelationEntity<BaseNodeEntity, BaseNodeEntity> r = (DomainRelationEntity<BaseNodeEntity, BaseNodeEntity>) row.get("r");

            relation.setId(r.getId());
            relation.setSource(r.getStartNode().getId());
            relation.setTarget(r.getEndNode().getId());
            relation.setName(r.getRelation());
            links.add(relation);

            DocumentEntity n = (DocumentEntity) row.get("n");
            DocumentEntity m = (DocumentEntity) row.get("m");
            set.add(n);
            set.add(m);
            cate.add(new Name(n.getCategory()));
            cate.add(new Name(m.getCategory()));

        }
        docAndRelation.setNodes(new ArrayList<>(set));
        docAndRelation.setLinks(links);
        docAndRelation.setCategories(new ArrayList<>(cate));

        return docAndRelation;
    }

    @Override
    public List<Map<String, Object>> queryProperty(String term) {
        return documentRepository.queryProperty(term);
    }
}
