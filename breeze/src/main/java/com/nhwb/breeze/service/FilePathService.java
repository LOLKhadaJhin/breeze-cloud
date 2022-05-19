package com.nhwb.breeze.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.FilePath;

import java.util.List;

/**
* @author 起风了
* @description 针对表【tb_file_path】的数据库操作Service
* @createDate 2022-05-01 23:10:20
*/
public interface FilePathService extends IService<FilePath> {
    boolean deleteByFileIds(List<Long> fileIds);
    boolean deleteByFileId(long fileId);
}
