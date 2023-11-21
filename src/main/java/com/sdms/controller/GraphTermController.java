package com.sdms.controller;

import com.sdms.common.lang.Result;
import com.sdms.graph_entity.TermEntity;
import com.sdms.graph_service.TermService;
import com.sdms.util.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("graph_term")
public class GraphTermController {

    @Autowired
    TermService termService;

    @Autowired
    FileHandler fileHandler;

    @PostMapping("/save")
    public Result saveTerm(@RequestBody TermEntity term) {

        termService.save(term);
        return Result.success("保存成功");
    }

    @PostMapping("/file")
    public Result fileUpload(@RequestParam("file") MultipartFile file) {

        String res = fileHandler.reserveFile(file);
        if (res.contains("error")) {
            return Result.fail(res);
        }
        System.out.println(res);
        termService.addByJsonFile(fileHandler.parseJson(res));

        return Result.success("保存成功");
    }

    @PostMapping("/delete_all")
    public Result deleteAll() {

        termService.deleteAll();
        return Result.success("删除成功");
    }

    @PostMapping("/get_term_by_id")
    public Result getTermById(@Param("id") Long id) {

        return Result.success(termService.getById(id));

    }

    @PostMapping("create_from")
    public Result createFrom() {
        Iterable<TermEntity> entities = termService.findAll();
        for (TermEntity term: entities) {
            termService.createRelation(term.getTerm(), "来源于", term.getSourceNumber());
        }
        return Result.success("创建成功");
    }

}
