package com.sdms.service;

import java.util.List;
import java.util.Set;

public interface LoginService {

    // 保存token
    boolean saveToken(String token,String username);

}
