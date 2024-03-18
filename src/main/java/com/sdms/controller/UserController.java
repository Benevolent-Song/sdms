package com.sdms.controller;


import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdms.common.lang.Result;
import com.sdms.entity.Documents;
import com.sdms.entity.User;
import com.sdms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LSY
 * @since 2022-04-16
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        if(Objects.equals(user.getUsername(), "") || Objects.equals(user.getPassword(), "")) {
            return Result.fail("用户名或密码不能为空");
        }
        user.setPassword(SecureUtil.md5(user.getPassword()));
        if(userService.save(user)) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        if(userService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        if(userService.updateById(user)) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

    @PostMapping("/queryAll")
    public Result queryAll(@RequestParam(defaultValue = "1") Integer currentPage) {
        Page<User> page = new Page<>(currentPage, 10);
        IPage<User> userIPage = userService.page(page, new QueryWrapper<User>().eq("status", 1));
        return Result.success(userIPage);
    }



}
