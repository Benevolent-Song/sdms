package com.sdms.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdms.entity.Documents;
import com.sdms.common.dto.QueryForm;
import com.sdms.entity.Term;
import com.sdms.mapper.DocumentsMapper;
import com.sdms.util.PdfToJsonUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TermService {

    private static final String ES_INDEX = "term";
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;
    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private DocumentsMapper documentsMapper;


    public Boolean parseObject(JSONObject jsonObject) throws IOException {

        JSONArray array = jsonObject.getJSONArray("terms");

        ArrayList<Term> list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Term term = new Term();
            term.setId(pdfToJsonUtil.getUUID());
            term.setPid(obj.getString("pid"));
            term.setChapter(obj.getString("chapter"));
            term.setPage(obj.getString("page"));
            term.setPage2(obj.getInteger("page2"));
            term.setTerm(obj.getString("term"));
            term.setTermEn(obj.getString("termEn"));
            term.setDefinition(obj.getString("definition"));

            list.add(term);
        }

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        for (Term term : list) {
            bulkRequest.add(
                    new IndexRequest(ES_INDEX)
                            .id(term.getId())
                            .source(JSON.toJSONString(term), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    public Map<String,Object> matchQuery(QueryForm queryForm, Integer pageIndex, Integer pageSize) throws IOException {

        ArrayList<String> pids = documentsService.searchByType(queryForm.getType(), queryForm.getDate());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("pid", pids));

        ArrayList<HashMap<String, String>> keywords = queryForm.getKeywords();

        if(!keywords.isEmpty()) {
            for (HashMap<String, String> keyword : keywords) {
                if (!keyword.get("text").isEmpty()) {
                    if (Objects.equals(keyword.get("method"), "and")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.must(QueryBuilders.multiMatchQuery(keyword.get("text"),"term","termEn"));
                        }else {
                            queryBuilder.must(QueryBuilders.matchPhraseQuery("term", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                        }
                    }
                    if (Objects.equals(keyword.get("method"), "or")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.should(QueryBuilders.multiMatchQuery(keyword.get("text"),"term","termEn"));
                        }else {
                            queryBuilder.should(QueryBuilders.matchPhraseQuery("term", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                        }
                    }
                }

            }
        }

        return doSearch(queryBuilder, pageIndex, pageSize);
    }

    public Map<String,Object> matchAll(Integer pageIndex, Integer pageSize) throws IOException {

        QueryBuilder matchQueryBuilder = QueryBuilders.matchAllQuery();
        return doSearch(matchQueryBuilder, pageIndex, pageSize);
    }

    public Map<String,Object> doSearch(QueryBuilder q, Integer pageIndex, Integer pageSize) throws IOException {

        SearchRequest term = new SearchRequest(ES_INDEX);
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.trackTotalHits(true);

        // 查询，并设置60s的超时时间
        searchSourceBuilder.query(q);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));// 60s
        // 分页
        searchSourceBuilder.from((pageIndex-1)*pageSize);
        searchSourceBuilder.size(pageSize);

        // 执行查询
        term.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(term, RequestOptions.DEFAULT);
        // 解析结果
        SearchHits hits = searchResponse.getHits();
        String totalHits = hits.getTotalHits().toString().split(" ")[0];
        List<Map<String,Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {

            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();

            String pid = (String) documentFields.getSourceAsMap().get("pid");
            QueryWrapper<Documents> wrapper = new QueryWrapper<Documents>().eq("pid",pid);

            String titleCn = documentsMapper.selectOne(wrapper).getTitleCn();
            String number = documentsMapper.selectOne(wrapper).getNumber();
            sourceAsMap.put("titleCn",titleCn);
            sourceAsMap.put("number",number);

            results.add(sourceAsMap);
        }

        Map<String,Object> res = new HashMap<>();
        res.put("records", results);
        res.put("total", Integer.parseInt(totalHits));
        res.put("pageIndex", pageIndex);
        res.put("pageSize", pageSize);

        return res;
    }

    public Boolean addTerm(Term term) throws IOException {

        IndexRequest request = new IndexRequest(ES_INDEX);
        term.setId(pdfToJsonUtil.getUUID());
        request.id(term.getId());
        request.timeout("1s");
        // 将我们的数据放入请求中
        request.source(JSON.toJSONString(term), XContentType.JSON);
        // 客户端发送请求，获取响应的结果
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        return Objects.equals(response.status().toString(), "CREATED");
    }

    public Boolean deleteTerm(String id) throws IOException {
        DeleteRequest request = new DeleteRequest(ES_INDEX, id);
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        return Objects.equals(response.status().toString(), "OK");
    }

    public Boolean updateTerm(Term term) throws IOException {
        String id = term.getId();
        UpdateRequest request = new UpdateRequest(ES_INDEX, id);
        request.doc(JSON.toJSONString(term),XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return Objects.equals(response.status().toString(), "OK");
    }

}
