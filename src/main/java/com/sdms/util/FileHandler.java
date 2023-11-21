package com.sdms.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class FileHandler {

    @Value("${filepath.graph.path}")
    private String fileTempPath;

    public String reserveFile(MultipartFile file) {

        if (file.isEmpty()) {
            return "error,空文件！";
        }
        // 文件名
        String fileName = file.getOriginalFilename();
        assert fileName != null;

        File fileTempObj = new File(fileTempPath + fileName);
        System.out.println(fileTempPath + fileName);
        System.out.println(fileTempObj);

        // 使用文件名称检测文件是否已经存在
        if (fileTempObj.exists()) {
            return "error,文件已经存在!";
        }

        try {

            file.transferTo(fileTempObj);

        } catch (Exception e) {
            return e.getMessage();
        }

        return fileTempObj.getName();

    }


    public JSONArray parseJson(String fileName) {

        BufferedReader reader = null;
        StringBuilder lastStr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(fileTempPath + fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //数据获取
                lastStr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String jsonString = lastStr.toString();

        return JSONObject.parseArray(jsonString);
    }

}
