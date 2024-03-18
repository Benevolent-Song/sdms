package com.sdms.controller;
import com.alibaba.fastjson.JSONObject;
import com.sdms.common.lang.Result;
import com.sdms.entity.EsDoc;
import com.sdms.common.dto.QueryForm;
import com.sdms.service.DocumentsService;
import com.sdms.service.EsService;
import com.sdms.util.PdfToJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@RestController
@RequestMapping("/es")
public class EsDocController {
    @Autowired
    private EsService esService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;
    @Autowired
    private DocumentsService documentsService;

    @PostMapping("/json")
    public Result parse(@RequestParam("file") MultipartFile file) throws IOException {
        String pid = pdfToJsonUtil.getUUID();
        JSONObject object = pdfToJsonUtil.parseJson(file);
        if (documentsService.save(pdfToJsonUtil.createDocument(object, pid))) {
            if (esService.parseObject(object, pid)) {
                return Result.success("解析json文件成功！pid:" + pid);
            }
        }
        return Result.fail("解析json文件失败！");
    }

    @PostMapping("/select/{pageIndex}/{pageSize}")
    public Result selectListByType(@RequestBody QueryForm queryForm,
                                   @PathVariable("pageIndex") Integer pageIndex,
                                   @PathVariable("pageSize") Integer pageSize) throws IOException {
        return Result.success(esService.boolQuery(queryForm, pageIndex, pageSize));
    }


    @GetMapping("/selectList/{pid}")
    public Result selectList(@PathVariable("pid") String pid) throws IOException {

        return Result.success(esService.selectList(pid));
    }

    @GetMapping("/search/{keyword}/{pageIndex}/{pageSize}")
    public Result highlightParse(@PathVariable("keyword") String keyword,
                                           @PathVariable("pageIndex") Integer pageIndex,
                                           @PathVariable("pageSize") Integer pageSize) throws IOException {

        return Result.success(esService.matchQuery(keyword, pageIndex, pageSize));
    }

    @GetMapping("/search/{keyword1}/{keyword2}/{pageIndex}/{pageSize}")
    public Result highlightParse(@PathVariable("keyword1") String keyword1,
                                 @PathVariable("keyword2") String keyword2,
                                 @PathVariable("pageIndex") Integer pageIndex,
                                 @PathVariable("pageSize") Integer pageSize) throws IOException {

        return Result.success(esService.boolQuery(keyword1, keyword2, pageIndex, pageSize));
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String id) throws IOException {
        if (esService.deleteEsDoc(id)) {
            return Result.success("删除文档成功");
        }
        return Result.fail("删除文档失败");
    }

    @PostMapping("/addOne")
    public Result addEsDoc(@RequestBody EsDoc esDoc) throws IOException {
        if(esService.addEsDoc(esDoc)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/update")
    public Result updateEsDoc(@RequestBody EsDoc esDoc) throws IOException {
        if(esService.updateEsDoc(esDoc)) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

}
