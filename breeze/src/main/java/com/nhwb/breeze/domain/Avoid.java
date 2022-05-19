package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @TableName tb_avoid
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avoid implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

    /**
     *
     */
    private String uuid;
    /**
     * 授权
     */
    private Boolean authorize;
}