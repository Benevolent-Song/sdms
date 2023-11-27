package com.sdms.controller;

import com.sdms.common.lang.Result;
import com.sdms.graph_entity.DocumentEntity;
import com.sdms.graph_service.DocumentService;
import com.sdms.util.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("graph_doc")
public class GraphDocController {

    @Autowired
    DocumentService documentService;
    @Autowired
    FileHandler fileHandler;

    @PostMapping("/get_node")
    public Result getNode(@RequestParam("number") String number) {

        return Result.success(documentService.queryNode(number));

    }

    @PostMapping("/get_all")
    public Result getAll() {

        Iterable<DocumentEntity> doc = documentService.getAll();

        return Result.success(doc);

    }

    @PostMapping("/save")
    public Result saveDoc(@RequestBody DocumentEntity doc) {

        documentService.saveDocument(doc);
        return Result.success("保存成功");

    }

    @PostMapping("/findByTitle")
    public Result findByTile(@RequestParam("title") String title) {

        return Result.success(documentService.findByTile(title));

    }

    @PostMapping("/findByCategory")
    public Result findByCategory(@RequestParam("category") String category) {

        return Result.success(documentService.findByCategory(category));

    }

    @PostMapping("/getCount")
    public Result getCount() {

        return Result.success(documentService.getCount());

    }

    @PostMapping("/delete")
    public Result deleteDoc(@RequestParam("id") long id) {

        documentService.deleteById(id);

        return Result.success("删除成功");
    }

    @PostMapping("/same_domain")
    public Result createDomain() {
        Iterable<DocumentEntity> entities = documentService.getAll();
        for (DocumentEntity doc1: entities) {
            for (DocumentEntity doc2: entities) {
                if (Objects.equals(doc1.getDomain(), doc2.getDomain()) && !Objects.equals(doc1.getId(), doc2.getId())) {
                    documentService.createRelation(doc1.getNumber(), "同领域", doc2.getNumber());
                }
            }
        }

        return Result.success("添加成功");
    }

    @PostMapping("/file")
    public Result fileUpload(@RequestParam("file") MultipartFile file) {

        String res = fileHandler.reserveFile(file);
        if (res.contains("error")) {
            return Result.fail(res);
        }
        documentService.parseJsonFile(fileHandler.parseJson(res));

        return Result.success("导入成功");
    }

}
