package com.sdms.service;

import com.sdms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LSY
 * @since 2022-04-16
 */
public interface UserService extends IService<User> {

    User getUser(String username);

}
