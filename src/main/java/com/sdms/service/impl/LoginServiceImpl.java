package com.sdms.service.impl;


import com.sdms.service.LoginService;
import org.springframework.stereotype.Service;


@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean saveToken(String token, String username) {
        return false;
    }


}
