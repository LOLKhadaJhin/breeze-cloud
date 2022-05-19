package com.nhwb.breeze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.FilePath;
import com.nhwb.breeze.mapper.FilePathMapper;
import com.nhwb.breeze.service.FilePathService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 起风了
 * @description 针对表【tb_file_path】的数据库操作Service实现
 * @createDate 2022-05-01 23:10:20
 */
@Service
public class FilePathServiceImpl extends ServiceImpl<FilePathMapper, FilePath> implements FilePathService {

    @Override
    public boolean deleteByFileIds(List<Long> fileIds) {
        if (fileIds != null && fileIds.size() > 0) {
            return remove(new QueryWrapper<FilePath>()
                    .lambda()
                    .in(FilePath::getFileId, fileIds)
            );
        }
        return true;
    }

    @Override
    public boolean deleteByFileId(long fileId) {
        return remove(new QueryWrapper<FilePath>()
                .lambda()
                .eq(FilePath::getFileId, fileId)
        );
    }
}
