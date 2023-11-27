package com.sdms.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.sdms.common.dto.LoginDto;
import com.sdms.common.lang.Result;
import com.sdms.entity.User;
import com.sdms.service.LoginService;
import com.sdms.service.UserService;
import com.sdms.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    LoginService loginService;

    /**
     * 默认账号密码：root / 111111
     *
     */
    @CrossOrigin
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) throws UnsupportedEncodingException {
        User user = userService.getUser(loginDto.getUsername());
        Assert.notNull(user, "用户不存在");
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            return Result.fail("密码错误！");
        }
        String jwt = JwtUtils.createToken(user);
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");
        // 用户可以另一个接口
        return Result.success(
                MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername()).put("role", user.getRole())
                .map()
        );
    }

    // 退出
    @GetMapping("/logout")
    @RequiresAuthentication
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success("注销成功！");
    }
}

