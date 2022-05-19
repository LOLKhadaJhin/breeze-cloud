package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @TableName tb_user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
//    @TableField(select = false)
    private String password;

    /**
     * 昵称
     */
    private String fullname;

    /**
     * 激活状态
     */
    private Boolean activation;
    /**
     * 上传权限
     */
    private Boolean upload;
    /**
     * 权限列表
     */
    @TableField(exist = false)
    private List<Long> grantPermissionIds;
}