package com.nhwb.breeze.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister implements Serializable {

    /**
     * 账号
     */
//    @NotEmpty
//    @Length(min = 6, max = 18, message = "长度6到18位")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,18}$", message = "(长度在6~18之间，只能包含字母、数字)")
    private String username;

    /**
     * 密码
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,18}$", message = "(长度6~18，只能包含字母、数字和下划线)")
    private String password;
    /**
     * 密码
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,18}$", message = "(长度在6~18之间)")
    private String again;
    /**
     * 昵称
     */
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9]{3,12}$", message = "(长度3~12，只能包含中文、英文、数字)")
    private String fullname;

    @NotNull(message = "(别搞事情，这个必须点)")//不可以为null
    @AssertTrue(message = "(别搞事情，这个必须点)")//可以为null
    private Boolean keep;


}
