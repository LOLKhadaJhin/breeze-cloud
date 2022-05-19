package com.nhwb.breeze.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilePath  implements Serializable {
    @TableId(type = IdType.INPUT)
    private String md5;
    private Long fileId;
    private String path;
}
