package com.sdms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogQueryFrom {
    private static final long serialVersionUID = 1L;

    private ArrayList<HashMap<String, String>> keywords;//关键词查询
    private ArrayList<String> date;//日期查询
    private ArrayList<String> people;//人员查询
}
