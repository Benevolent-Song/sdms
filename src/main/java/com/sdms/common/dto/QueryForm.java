package com.sdms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, ArrayList<String>> type;

    private ArrayList<HashMap<String, String>> keywords;

    private ArrayList<String> date;

}

