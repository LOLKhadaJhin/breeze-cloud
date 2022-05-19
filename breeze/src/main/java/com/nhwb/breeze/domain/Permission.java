package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName tb_permission
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {
    /**
     * 我们直接用id作为权限标识符
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限标识符
     */
    @TableField(select = false)
    private String code;

    /**
     * 描述/名称
     */
    private String description;
    /**
     * 所有人公开
     */
    private Boolean overt;
    /**
     * 激活用户公开
     */
    private Boolean activation;
}