package com.sdms.shiro;


import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AccountProfile implements Serializable {

    private Long id;

    private String username;

    private Set<String> perms;

}
