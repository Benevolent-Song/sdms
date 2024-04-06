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

    @PostMapping("/json")//上传导出的json文件,
    public Result parse(@RequestParam("file") MultipartFile file) throws IOException {

            JSONObject object = pdfToJsonUtil.parseJson(file);
            if (esService.parsemyjson(object))//解析json对象,将文本根据chapter分成一段段,最后一次性提交
            {return Result.success("解析json文件成功！");}

        return Result.fail("解析json文件失败！");
    }

    @PostMapping("/select/{pageIndex}/{pageSize}")//根据请求条件查询es
    public Result selectListByType(@RequestBody QueryForm queryForm,
                                   @PathVariable("pageIndex") Integer pageIndex,
                                   @PathVariable("pageSize") Integer pageSize) throws IOException {
        return Result.success(esService.boolQuery(queryForm, pageIndex, pageSize));
    }


    @GetMapping("/selectList/{pid}")//请求查看一篇文章的所有段落,根据pid在es中查找,相同pid是同一个文章
    public Result selectList(@PathVariable("pid") String pid) throws IOException {
        return Result.success(esService.selectList(pid));//返回的是多条pid相同的文段段落,但它们的id名不相同
    }

    @GetMapping("/search/{keyword}/{pageIndex}/{pageSize}")//多条件查询
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

    @PostMapping("/update")//更改段落的内容
    public Result updateEsDoc(@RequestBody EsDoc esDoc) throws IOException {
        if(esService.updateEsDoc(esDoc)) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

}
