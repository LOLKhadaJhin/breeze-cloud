package com.nhwb.breeze.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionFileResult implements Serializable {
    /**
     * 文件夹名
     */
    private String fileName;
    /**
     * 文件URI
     */
    private String md5;
}
