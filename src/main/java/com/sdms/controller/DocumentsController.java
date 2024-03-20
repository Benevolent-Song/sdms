package com.sdms.controller;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdms.common.dto.QueryForm;
import com.sdms.common.lang.Result;
import com.sdms.entity.Documents;
import com.sdms.entity.Logs;
import com.sdms.service.DocumentsService;
import com.sdms.service.EsService;
import com.sdms.service.LogService;
import com.sdms.util.PdfToJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LSY
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/docs")
public class DocumentsController {

    @Value("${filepath.pdf.path}")
    private String uploadFilePath;
    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private EsService esService;
    @Autowired
    private LogService logService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;


    @GetMapping("/queryAll")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        Page<Documents> page = new Page<>(currentPage, 10);
        IPage<Documents> pageData = documentsService.page(page, new QueryWrapper<Documents>().orderByDesc("id"));

        return Result.success(pageData);
    }

    @PostMapping("/query")
    public Result listByCategory(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize, @RequestBody QueryForm queryForm) {

        HashMap<String, ArrayList<String>> type = queryForm.getType();
        String keyword = queryForm.getKeywords().get(0).get("text");
        ArrayList<String> date = queryForm.getDate();

        Page<Documents> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Documents> queryWrapper = documentsService.makeQuery(type, date);

        if (!keyword.isEmpty()) {
            String [] arr = keyword.split("\\s+");
            if (arr.length == 1) {
                queryWrapper.and(wrapper -> wrapper.like("titleCn", arr[0]).or().like("number", keyword));
            }
            if (arr.length >= 2) {
                queryWrapper.and(wrapper -> wrapper.like("number", keyword)
                        .or(wrapper1 -> wrapper1.like("titleCn", arr[0]).like("titleCn", arr[1])));
            }

        }
        queryWrapper.orderByDesc("releaseDate");

        IPage<Documents> pageData = documentsService.page(page, queryWrapper);
        return Result.success(pageData);
    }

    @PostMapping("/update")
    public Result updateDocument(@RequestBody Documents document) {
        if (documentsService.updateById(document)) {
            Logs logs=new Logs();
            LocalDateTime now = LocalDateTime.now();//获取当前时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//定义日期时间格式
            String currentDateTime = now.format(formatter); //使用格式化器格式化日期时间
            logs.setDotime(currentDateTime);
            logs.setTitleCn(document.getTitleCn());
            logs.setNumber(document.getNumber());
            logs.setDoclass("更改文档数据");
            logs.setPeople("");//人员
            //logs.setWhatdo("");//操作的内容
            logService.saveOrUpdate(logs);
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }


    @PostMapping("/delete/{id}")
    public Result deleteDocument(@PathVariable Integer id) throws IOException {

        Documents doc = documentsService.getById(id);
        String fileName = doc.getPid();
        String path = uploadFilePath + "/" + fileName + ".pdf";
        FileSystemUtils.deleteRecursively(new File(path));

        if (esService.deleteBatch(fileName)) {
            LocalDateTime now = LocalDateTime.now();//获取当前时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//定义日期时间格式
            String currentDateTime = now.format(formatter); //使用格式化器格式化日期时间
            Logs logs=new Logs();
            logs.setDotime(currentDateTime);
            logs.setTitleCn(doc.getTitleCn());
            logs.setNumber(doc.getNumber());
            logs.setDoclass("删除文档");
            logs.setPeople("");//人员
            //logs.setWhatdo("");//操作的内容
            logService.saveOrUpdate(logs);
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/batch")
    public Result addBatch(@RequestParam("file") MultipartFile file) {

        JSONObject object = pdfToJsonUtil.parseJson(file);
        ArrayList<Documents> list = documentsService.parseObject(object);
        //System.err.println("file:"+file);
        //System.err.println("object:"+object);
        if(documentsService.saveBatch(list)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");

    }

}
