package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @TableName tb_permission_file
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionFile implements Serializable {
    /**
     * 文件夹id
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;
    /**
     * 权限id
     */
    private Long permissionId;

    /**
     * 文件夹
     */
    private String file;
}