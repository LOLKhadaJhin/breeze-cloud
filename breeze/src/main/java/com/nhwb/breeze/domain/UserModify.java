package com.nhwb.breeze.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModify implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,18}$", message = "(密码不正确)")
    private String old;
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,18}$", message = "(长度6~18，只能包含字母、数字和下划线)")
    private String password;
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,18}$", message = "(长度6~18，只能包含字母、数字和下划线)")
    private String again;
}
