package com.sdms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

//存放前端传送来的文件查询条件(包括名称,日期,标题,文件类型,文件分组等)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, ArrayList<String>> type;

    private ArrayList<HashMap<String, String>> keywords;

    private ArrayList<String> date;

}

