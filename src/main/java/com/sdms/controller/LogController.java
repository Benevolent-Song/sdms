package com.sdms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdms.common.dto.QueryForm;
import com.sdms.common.lang.Result;
import com.sdms.entity.Logs;
import com.sdms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

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
    @PostMapping("/delete/{id}")
    public Result deleteLog(@PathVariable Integer id) throws IOException {
        if (logService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    //输入条件点击按键进行查询
    @PostMapping("/query")
    public Result listByCategory(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize, @RequestBody QueryForm queryForm) {

        Page<Logs> page = new Page<>(currentPage, pageSize);
        IPage<Logs> pageData = logService.page(page, null);
        return Result.success(pageData);
    }
}
