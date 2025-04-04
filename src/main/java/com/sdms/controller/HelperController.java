package com.sdms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdms.common.lang.Result;
import okhttp3.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/helper")
public class HelperController {
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @CrossOrigin
    @PostMapping("/ask")//由于okhttp3里面也有RequestBody类,需要区分@RequestBody,所以写成@org.springframework.web.bind.annotation.RequestBody
    public Result selectListByType(@org.springframework.web.bind.annotation.RequestBody String ask) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        ask=ask.replace("{", "").replace("}", "");
        String content=String.format("{\"app_id\":\"9d46f906-732a-4028-9733-b35117dcc753\",%s,\"stream\":false,\"conversation_id\":\"cd25cb31-c4eb-4e70-a8df-2a86f002f1d8\"}",ask);
        System.out.println(content);
        RequestBody body = RequestBody.create(mediaType,content);
        Request request = new Request.Builder()
                .url("https://qianfan.baidubce.com/v2/app/conversation/runs")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Appbuilder-Authorization", "Bearer bce-v3/ALTAK-Kf5Omuj2rKaMfvG18pncj/9d276e4119bcb2f1f053c8cf8ec3ccb17b3c0924")
                .build();

        // 创建一个 OkHttpClient.Builder 并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        // 设置连接超时时间，例如 10 秒
        httpClientBuilder.connectTimeout(50, TimeUnit.SECONDS);
        // 设置读取超时时间，例如 30 秒
        httpClientBuilder.readTimeout(50, TimeUnit.SECONDS);
        // 设置写入超时时间（如果需要的话），例如 10 秒
        httpClientBuilder.writeTimeout(100, TimeUnit.SECONDS);
        // 创建 OkHttpClient 实例
        OkHttpClient HTTP_CLIENT = httpClientBuilder.build();

        Response response = HTTP_CLIENT.newCall(request).execute();

        // 使用Jackson将字符串解析为JSON对象
        ObjectMapper objectMapper = new ObjectMapper();
        // 如果你事先知道JSON的具体类型，可以直接使用那个类型，而不是TypeReference
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        System.out.println(jsonNode);

        return Result.success(jsonNode);//返回响应给前端,响应成功码为code=200,数据在data字段中
    }
}
