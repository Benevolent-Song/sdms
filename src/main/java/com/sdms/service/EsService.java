package com.sdms.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdms.entity.Documents;
import com.sdms.entity.EsDoc;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
@Service
public class EsService {

    private static final String ES_INDEX = "sdms";
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private DocumentsMapper documentsMapper;
    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;


    //组合查询
    public Map<String,Object> boolQuery(QueryForm queryForm, Integer pageIndex, Integer pageSize) throws IOException {

        ArrayList<String> pids = documentsService.searchByType(queryForm.getType(), queryForm.getDate());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("pid", pids));
        //es构造器BoolQueryBuilder,为es提供相当于mybaits对于mysql的作用。
        ArrayList<HashMap<String, String>> keywords = queryForm.getKeywords();

        if(!keywords.isEmpty()) {
            for (HashMap<String, String> keyword : keywords) {
                if (!keyword.get("text").isEmpty()) {
                    if (Objects.equals(keyword.get("method"), "and")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.must(QueryBuilders.matchQuery("text", keyword.get("text")));
                        }else {
                            queryBuilder.must(QueryBuilders.matchPhraseQuery("text", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                        }
                    }
                    if (Objects.equals(keyword.get("method"), "or")) {
                        if (Objects.equals(keyword.get("analyzer"), "yes")) {
                            queryBuilder.should(QueryBuilders.matchQuery("text", keyword.get("text")));
                        }else {
                            queryBuilder.should(QueryBuilders.matchPhraseQuery("text", keyword.get("text")).slop(5).analyzer("ik_max_word"));
                            //搜索文档中字段 "text" 中包含与指定短语匹配的文本，并且允许这些短语中的词之间最多有5个其他词。同时，使用 IK 分析器来处理文本。
                            //must相当于and,should相当于or
                        }
                    }
                }
            }
        }
        return doSearch(queryBuilder, pageIndex, pageSize);
        //!!!返回了部分mysql中查询的信息("titleCn","number","page2"),放回到响应中
    }

    // 多条件查询
    public Map<String,Object> boolQuery(String keyword1, String keyword2, Integer pageIndex, Integer pageSize) throws IOException {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("text", keyword1))
                .must(QueryBuilders.matchQuery("text", keyword2));
        return doSearch(queryBuilder, pageIndex, pageSize);
    }

    public Map<String,Object> matchQuery(String keyword, Integer pageIndex, Integer pageSize) throws IOException {
        QueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"text");
        return doSearch(matchQueryBuilder, pageIndex, pageSize);
    }

    // ！！！！高亮查询,上面的方法都调用了它作为返回结果
    public Map<String,Object> doSearch(QueryBuilder q, Integer pageIndex, Integer pageSize) throws IOException {

        SearchRequest sdms = new SearchRequest(ES_INDEX);//构建SearchRequest请求对象，指定索引库
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 显示真实返回条数
        searchSourceBuilder.trackTotalHits(true);

        // 查询，并设置60s的超时时间
        searchSourceBuilder.query(q);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));// 60s
        // 分页
        searchSourceBuilder.from((pageIndex-1)*pageSize);
        searchSourceBuilder.size(pageSize);
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("text").fragmentSize(2000);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 执行查询
        sdms.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(sdms, RequestOptions.DEFAULT);
        // 解析结果
        SearchHits hits = searchResponse.getHits();//提取es请求中的hit中的内容
        String totalHits = hits.getTotalHits().toString().split(" ")[0];
        List<Map<String,Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            // 使用新的字段值（高亮），覆盖旧的字段值
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField text = highlightFields.get("text");

            // 替换标题,编号
            String pid = (String) documentFields.getSourceAsMap().get("pid");
            String page = (String) documentFields.getSourceAsMap().get("page");
            QueryWrapper<Documents> wrapper = new QueryWrapper<>();
            wrapper.eq("pid",pid);
            String titleCn = documentsMapper.selectOne(wrapper).getTitleCn();
            String number = documentsMapper.selectOne(wrapper).getNumber();
            int offset = documentsMapper.selectOne(wrapper).getOffset();
            int page2 = 0;
            if(!page.isEmpty()){
                try {
                    page2 = Integer.parseInt(page.trim()) + offset;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
            sourceAsMap.put("titleCn",titleCn);
            sourceAsMap.put("number",number);
            sourceAsMap.put("page2",page2);

            // 替换
            if (text != null){
                Text[] fragments = text.fragments();
                StringBuilder new_name = new StringBuilder();
                for (Text str : fragments) {
                    new_name. append(str);
                }
                sourceAsMap.put("text",new_name.toString());
            }

            results.add(sourceAsMap);
        }
        Map<String,Object> res = new HashMap<>();
        res.put("records", results);
        res.put("total", Integer.parseInt(totalHits));
        res.put("pageIndex", pageIndex);
        res.put("pageSize", pageSize);

        return res;
    }
    public Boolean parsemyjson(JSONObject jsonObject)throws IOException
    {
        JSONArray array = jsonObject.getJSONArray("content");//json文件的开头,后面跟着的才是json数据

        ArrayList<EsDoc> list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            EsDoc doc = new EsDoc();
            doc.setPid(obj.getString("pid"));//设置为文档中设定的pid值
            doc.setChapter(obj.getString("chapter"));
            doc.setPage(obj.getString("page"));
            doc.setText(obj.getString("text"));
            doc.setId(obj.getString("id"));//设定为文档中设定的id值
            list.add(doc);
        }

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        for (EsDoc doc : list) {
            bulkRequest.add(
                    new IndexRequest(ES_INDEX)
                            .id(doc.getId())
                            .source(JSON.toJSONString(doc), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }
    // 解析json对象，将内容写入es
    public Boolean parseObject(JSONObject jsonObject, String pid) throws IOException {

        JSONArray array = jsonObject.getJSONArray("content");

        ArrayList<EsDoc> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            EsDoc doc = new EsDoc();
            doc.setPid(pid);
            doc.setChapter(obj.getString("chapter"));
            doc.setPage(obj.getString("page"));
            doc.setText(obj.getString("text"));
            doc.setId(pid + (i + 1));
            list.add(doc);
        }
//        System.out.println(array.size());

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        // 批量请求处理
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(
                    // 这里是数据信息
                    new IndexRequest(ES_INDEX)
                            .id(list.get(i).getId()) // 没有设置id 会自定生成一个随机id
                            .source(JSON.toJSONString(list.get(i)),XContentType.JSON)
            );
        }
        //批量处理BulkRequest，其本质就是将多个普通的CRUD请求组合在一起发送。
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        // System.out.println(bulk.getTook());

        return !bulk.hasFailures();
    }

    public Boolean addEsDoc(EsDoc esDoc) throws IOException {

        IndexRequest request = new IndexRequest(ES_INDEX);//构建SearchRequest请求对象，指定索引库
        esDoc.setId(pdfToJsonUtil.getUUID());
        request.id(esDoc.getId());
        request.timeout("1s");
        // 将我们的数据放入请求中
        request.source(JSON.toJSONString(esDoc), XContentType.JSON);
        // 客户端发送请求，获取响应的结果
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        return Objects.equals(response.status().toString(), "CREATED");
    }

    // 根据id删除es文档
    public Boolean deleteEsDoc(String id) throws IOException {
        DeleteRequest request = new DeleteRequest(ES_INDEX, id);
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);

        return Objects.equals(response.status().toString(), "OK");
    }

    public Map<String,Object> selectList(String pid) throws IOException {

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("pid", pid);//设置查询的条件
        SearchRequest sr = new SearchRequest(ES_INDEX);
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.trackTotalHits(true);

        // 查询，并设置60s的超时时间
        searchSourceBuilder.query(matchQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));// 60s

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);

        // 执行查询
        sr.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(sr, RequestOptions.DEFAULT);

        // 解析结果
        SearchHits hits = searchResponse.getHits();
        String totalHits = hits.getTotalHits().toString().split(" ")[0];
        List<Map<String,Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            results.add(sourceAsMap);
        }

        // results = sortChapterUtil.sortChapterList(results);

        Map<String,Object> res = new HashMap<>();
        res.put("records", results);
        res.put("total", Integer.parseInt(totalHits));

        return res;

    }

    // 根据pid批量删除对应文件所有的文档
    public Boolean deleteBatch(String pid) throws IOException {
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest(ES_INDEX);//构建SearchRequest请求对象，指定索引库
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 匹配查询
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("pid",pid);

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // (3)条件投入
        searchSourceBuilder.query(matchQueryBuilder);
        searchSourceBuilder.size(10000);
        // 3.添加条件到请求
        searchRequest.source(searchSourceBuilder);
        // 4.客户端查询请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        SearchHits hits = search.getHits();
        // 将结果中的hits取出
        String h = JSON.toJSONString(hits);
        JSONObject j = JSONObject.parseObject(h);
        JSONArray arr = j.getJSONArray("hits");
        // 取出hits列表中所有的id
        ArrayList<String> list = new ArrayList<>();
        for (Object o : arr) {
            JSONObject hit = (JSONObject) o;
            list.add(hit.getString("id"));
        }

        DeleteRequest request = new DeleteRequest(ES_INDEX);
        for (String s : list) {
            request.id(s);
            request.timeout("1s");
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        }

        return true;
    }

    // 更新单个文档
    public Boolean updateEsDoc(EsDoc esDoc) throws IOException {
        String id = esDoc.getId();
        UpdateRequest request = new UpdateRequest(ES_INDEX, id);
        request.doc(JSON.toJSONString(esDoc),XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return Objects.equals(response.status().toString(), "OK");
    }
}
