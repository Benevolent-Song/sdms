package com.sdms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdms.common.dto.LogQueryFrom;
import com.sdms.common.lang.Result;
import com.sdms.entity.Documents;
import com.sdms.entity.Logs;
import com.sdms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    //刷新页面自动发送请求
    @PostMapping("/queryAll")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {
        Page<Logs> page = new Page<>(currentPage, 10);
        IPage<Logs> pageData = logService.page(page, new QueryWrapper<Logs>().orderByDesc("id"));
        System.out.println(pageData);
        return Result.success(pageData);
    }
    //输入条件点击按键进行查询
    @PostMapping("/query")
    public Result listByCategory(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize, @RequestBody LogQueryFrom logqueryForm) {

        String keyword = logqueryForm.getKeywords().get(0).get("text");
        ArrayList<String> date = logqueryForm.getDate();

        Page<Logs> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Logs> queryWrapper = logService.makeQuery(date);

        if (!keyword.isEmpty()) {
            String [] arr = keyword.split("\\s+");//对keyword按空格进行分割,并存储到arr数组中
            if (arr.length == 1)//如果只有一个单词时
            {
                queryWrapper.and(wrapper -> wrapper.like("titleCn",arr[0])
                        .or().like("number", arr[0])
                        .or().like("people", arr[0]));
            }
            if (arr.length >= 2)//如果有两个空格分开的关键词
            {
                queryWrapper.and(wrapper -> wrapper.like("number", keyword)
                        .or(wrapper1 -> wrapper1.like("titleCn", arr[0]).like("titleCn", arr[1])));
            }
            if (arr.length >= 3)//如果有三个空格分开的关键词
            {
                queryWrapper.and(wrapper -> wrapper.like("number", keyword)
                        .or(wrapper1 -> wrapper1.like("people",arr[0]).like("people", arr[1]))
                        .or(wrapper2 -> wrapper2.like("titleCn", arr[0]).like("titleCn", arr[1])));
            }
        }
        queryWrapper.orderByDesc("dotime");
        IPage<Logs> pageData = logService.page(page, queryWrapper);

        return Result.success(pageData);
    }

    @PostMapping("/delete/{id}")
    public Result deleteLog(@PathVariable Integer id) throws IOException {
        if (logService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }
}
