package com.sdms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdms.common.lang.Result;
import com.sdms.entity.Documents;
import com.sdms.entity.Logs;
import com.sdms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/query")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        Page<Logs> page = new Page<>(currentPage, 10);
        IPage<Logs> pageData = logService.page(page, new QueryWrapper<Logs>().orderByDesc("id"));
        System.out.println(pageData);
        return Result.success(pageData);
    }
}
