package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName tb_user_permission
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermission implements Serializable {
    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long permissionId;
    /**
     * 授权
     */
    private Boolean authorize;
}