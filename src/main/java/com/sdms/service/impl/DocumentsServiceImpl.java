package com.sdms.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdms.entity.Documents;
import com.sdms.entity.EsDoc;
import com.sdms.mapper.DocumentsMapper;
import com.sdms.service.DocumentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LSY
 * @since 2022-04-25
 */
@Service
public class DocumentsServiceImpl extends ServiceImpl<DocumentsMapper, Documents> implements DocumentsService {

    @Autowired
    private DocumentsMapper documentsMapper;

    public QueryWrapper<Documents> makeQuery(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date) {

        ArrayList<String> category = queryParams.get("category");
        ArrayList<String> domain = queryParams.get("domain");

        QueryWrapper<Documents> queryWrapper = new QueryWrapper<>();

        if (!category.isEmpty()) {
            if(category.size() == 1) {
                queryWrapper.and(wrapper ->wrapper.eq("category", category.get(0)));
            }
            if(category.size() == 2) {
                queryWrapper.and(wrapper -> wrapper.eq("category", category.get(0))
                        .or().eq("category", category.get(1)));
            }
            if(category.size() == 3) {
                queryWrapper.and(wrapper -> wrapper.eq("category", category.get(0))
                        .or().eq("category", category.get(1))
                        .or().eq("category", category.get(2)));
            }
            if(category.size() == 4) {
                queryWrapper.and(wrapper -> wrapper.eq("category", category.get(0))
                        .or().eq("category", category.get(1))
                        .or().eq("category", category.get(2))
                        .or().eq("category", category.get(3)));
            }
            if(category.size() == 5) {
                queryWrapper.and(wrapper -> wrapper.eq("category", category.get(0))
                        .or().eq("category", category.get(1))
                        .or().eq("category", category.get(2))
                        .or().eq("category", category.get(3))
                        .or().eq("category", category.get(4)));
            }
            if(category.size() == 6) {
                queryWrapper.and(wrapper -> wrapper.eq("category", category.get(0))
                        .or().eq("category", category.get(1))
                        .or().eq("category", category.get(2))
                        .or().eq("category", category.get(3))
                        .or().eq("category", category.get(4))
                        .or().eq("category", category.get(5)));
            }

        }
        if (!domain.isEmpty()) {
            if(domain.size() == 1) {
                queryWrapper.and(wrapper -> wrapper.eq("domain", domain.get(0)));
            }
            if(domain.size() == 2) {
                queryWrapper.and(wrapper -> wrapper.eq("domain", domain.get(0))
                        .or().eq("domain", domain.get(1)));
            }
            if(domain.size() == 3) {
                queryWrapper.and(wrapper -> wrapper.eq("domain", domain.get(0))
                        .or().eq("domain", domain.get(1))
                        .or().eq("domain", domain.get(2)));
            }
        }
        if (date.size() > 1) {
            queryWrapper.and(wrapper -> wrapper.between("releaseDate", date.get(0), date.get(1)));
        }

        return queryWrapper;
    }

    public ArrayList<String> searchByType(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date) {

        QueryWrapper<Documents> queryWrapper = makeQuery(queryParams, date);
        ArrayList<String> pids = new ArrayList<>();

        for (Documents document : documentsMapper.selectList(queryWrapper)) {
            String pid = document.getPid();
            pids.add(pid);
        }
        return pids;
    }

    public ArrayList<String> searchNumber(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date) {

        QueryWrapper<Documents> queryWrapper = makeQuery(queryParams, date);
        ArrayList<String> nums = new ArrayList<>();

        for (Documents document : documentsMapper.selectList(queryWrapper)) {
            String number = document.getNumber();
            nums.add(number);
        }
        return nums;
    }

    public ArrayList<Documents> parseObject(JSONObject jsonObject) {

        JSONArray array = jsonObject.getJSONArray("content");

        ArrayList<Documents> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Documents doc = new Documents();
            doc.setId(obj.getInteger("id"));
            doc.setPid(obj.getString("pid"));
            doc.setCategory(obj.getString("category"));
            doc.setNumber(obj.getString("pid"));
            doc.setTitleCn(obj.getString("titleCn"));
            doc.setTitleEn(obj.getString("titleEn"));
            doc.setIssuedBy(obj.getString("issuedBy"));
            doc.setReleaseDate(obj.getString("releaseDate"));
            doc.setImplementDate(obj.getString("implementDate"));
            doc.setDomain(obj.getString("domain"));
            doc.setOffset(obj.getInteger("offset"));

            list.add(doc);
        }

        return list;
    }


}
