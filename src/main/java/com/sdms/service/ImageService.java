package com.sdms.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdms.entity.Image;
import com.sdms.common.dto.QueryForm;
import com.sdms.util.PdfToJsonUtil;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ImageService {

    private static final String ES_INDEX = "images";
    @Value("D:/广电文献管理系统/数据和操作步骤/images")
    private String imagePath;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;


    public Boolean parseObject(JSONObject jsonObject) throws IOException {

        JSONArray array = jsonObject.getJSONArray("content");

        ArrayList<Image> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Image image = new Image();
            image.setId(pdfToJsonUtil.getUUID());
            image.setTitle(obj.getString("title"));
            image.setChapter(obj.getString("chapter"));
            image.setPage(obj.getInteger("page"));
            image.setPage2(obj.getInteger("page2"));
            image.setNumber(obj.getString("number"));
            image.setName(obj.getString("name"));
            image.setPath(obj.getString("path"));

            list.add(image);
        }

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        for (Image image : list) {
            bulkRequest.add(new IndexRequest(ES_INDEX).source(JSON.toJSONString(image), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    public Map<String,Object> matchQuery(QueryForm queryForm,String type, Integer pageIndex, Integer pageSize) throws IOException {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery("name", type));

        ArrayList<HashMap<String, String>> keywords = queryForm.getKeywords();

        if(!keywords.isEmpty()) {
            for (HashMap<String, String> keyword : keywords) {
                if (!keyword.get("text").isEmpty()) {
                    if (Objects.equals(keyword.get("method"), "and")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.must(QueryBuilders.matchQuery("title", keyword.get("text")));
                        }else {
                            queryBuilder.must(QueryBuilders.matchPhraseQuery("title", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                        }
                    }
                    if (Objects.equals(keyword.get("method"), "or")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.should(QueryBuilders.matchQuery("title", keyword.get("text")));
                        }else {
                            queryBuilder.should(QueryBuilders.matchPhraseQuery("title", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                        }
                    }
                }

            }
        }

        return doSearch(queryBuilder, pageIndex, pageSize);
    }

    public Map<String,Object> doSearch(QueryBuilder q, Integer pageIndex, Integer pageSize) throws IOException {

        SearchRequest image = new SearchRequest(ES_INDEX);
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
        image.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(image, RequestOptions.DEFAULT);

        // 解析结果
        SearchHits hits = searchResponse.getHits();
        String totalHits = hits.getTotalHits().toString().split(" ")[0];
        List<Map<String,Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            results.add(sourceAsMap);
        }

        Map<String,Object> res = new HashMap<>();
        res.put("records", results);
        res.put("total", Integer.parseInt(totalHits));
        res.put("pageIndex", pageIndex);
        res.put("pageSize", pageSize);

        return res;
    }

    public Boolean downloadFile(HttpServletResponse response, String filePath) {
        File file = new File(imagePath + filePath);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(filePath, "UTF-8"));
                ServletOutputStream outputStream = response.getOutputStream();

                FileCopyUtils.copy(fileInputStream, outputStream);
                return true;
            } catch (IOException e) {
                log.error("download file error: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }


}
