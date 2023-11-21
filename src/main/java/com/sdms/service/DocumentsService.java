package com.sdms.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdms.entity.Documents;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LSY
 * @since 2022-04-25
 */
public interface DocumentsService extends IService<Documents> {

    QueryWrapper<Documents> makeQuery(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date);

    ArrayList<String> searchByType(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date);

    ArrayList<String> searchNumber(HashMap<String, ArrayList<String>> queryParams, ArrayList<String> date);

    ArrayList<Documents> parseObject(JSONObject jsonObject);

}
