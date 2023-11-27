package com.sdms.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdms.common.lang.Result;
import com.sdms.common.dto.QueryForm;
import com.sdms.entity.Term;
import com.sdms.service.TermService;
import com.sdms.util.PdfToJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/term")
public class TermController {

    @Autowired
    private TermService termService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;

    @PostMapping("/json")
    public Result parse(@RequestParam("file") MultipartFile file) throws IOException {

        JSONObject object = pdfToJsonUtil.parseJson(file);

        if (termService.parseObject(object)) {
            return Result.success("解析json文件成功！");
        }

        return Result.fail("解析json文件失败！");
    }

    @PostMapping("/search/{pageIndex}/{pageSize}")
    public Result search(@RequestBody QueryForm queryForm,
                         @PathVariable("pageIndex") Integer pageIndex,
                         @PathVariable("pageSize") Integer pageSize) throws IOException {

        return Result.success(termService.matchQuery(queryForm, pageIndex, pageSize));
    }

    @PostMapping("/searchAll/{pageIndex}/{pageSize}")
    public Result search(@PathVariable("pageIndex") Integer pageIndex,
                         @PathVariable("pageSize") Integer pageSize) throws IOException {

        return Result.success(termService.matchAll(pageIndex, pageSize));
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String id) throws IOException {
        if (termService.deleteTerm(id)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/add")
    public Result addEsDoc(@RequestBody Term term) throws IOException {
        if(termService.addTerm(term)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/update")
    public Result updateEsDoc(@RequestBody Term term) throws IOException {
        if(termService.updateTerm(term)) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

}
