package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员
 * 作者：B站「怒火无边」
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseConfig implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

    /**
     * 允许注册
     */
    private Boolean register;

    /**
     * 自动激活
     */
    private Boolean activation;

    /**
     * 下载模式
     */
    private Boolean download;

    /**
     * 允许上传
     */
    private Boolean upload;

    /**
     * 缓存目录
     */
    private String repository;

    /**
     * 背景图片文件夹
     */
    private String backgroundDirectory;

    /**
     * 背景图片
     */
    private String background;
    /**
     * 所有背景图片
     */
    @TableField(exist = false)
    private Map<String, String> backgroundMd5;
    /**
     * 登录超时
     */
    private Integer timeout;

}
