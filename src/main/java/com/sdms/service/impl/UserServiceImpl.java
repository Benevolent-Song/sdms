package com.sdms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdms.entity.User;
import com.sdms.mapper.UserMapper;
import com.sdms.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LSY
 * @since 2022-04-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public User getUser(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

}
