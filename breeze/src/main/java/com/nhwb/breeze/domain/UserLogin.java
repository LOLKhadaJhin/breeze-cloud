package com.nhwb.breeze.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogin implements Serializable {
    //账号
    private String username;
    //密码
    private String password;
    //好人
    private Boolean keep = false;
    //保持登录
    private Boolean save = false;
}
