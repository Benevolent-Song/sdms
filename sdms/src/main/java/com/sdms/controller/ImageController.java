package com.sdms.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdms.common.lang.Result;
import com.sdms.common.dto.QueryForm;
import com.sdms.service.ImageService;
import com.sdms.util.PdfToJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;

    @PostMapping("/json")
    public Result parse(@RequestParam("file") MultipartFile file) throws IOException {

        JSONObject object = pdfToJsonUtil.parseJson(file);

        if (imageService.parseObject(object)) {
            return Result.success("解析json文件成功！");
        }

        return Result.fail("解析json文件失败！");
    }

    @PostMapping("/search/{type}/{pageIndex}/{pageSize}")
    public Result search(@RequestBody QueryForm queryForm,
                         @PathVariable("type") String type,
                         @PathVariable("pageIndex") Integer pageIndex,
                         @PathVariable("pageSize") Integer pageSize) throws IOException {

        return Result.success(imageService.matchQuery(queryForm, type, pageIndex, pageSize));
    }

    @GetMapping("/download")
    public Result download(HttpServletResponse response, @RequestParam(value = "filePath") String filePath) {
        if(imageService.downloadFile(response, filePath)) {
            return Result.success("done!");
        }
        return Result.fail("fail");
    }

}
