package com.nhwb.breeze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.PermissionFile;
import com.nhwb.breeze.mapper.PermissionFileMapper;
import com.nhwb.breeze.service.PermissionFileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 怒火无边
 * @description 针对表【tb_permission_file】的数据库操作Service实现
 * @createDate 2022-04-03 18:42:46
 */
@Service
public class PermissionFileServiceImpl extends ServiceImpl<PermissionFileMapper, PermissionFile> implements PermissionFileService {

    @Override
    public List<PermissionFile> listByPermissionIds(List<Long> ids) {
        return ids.size() == 0 ? null : list(new QueryWrapper<PermissionFile>()
                .lambda()
                .in(PermissionFile::getPermissionId, ids));
    }

    @Override
    public List<PermissionFile> listByPermissionId(long permissionId) {
        return list(new QueryWrapper<PermissionFile>()
                .lambda()
                .eq(PermissionFile::getPermissionId, permissionId));
    }

    @Override
    public List<Long> fileIdsByPermissionIds(List<Long> ids) {
        if (ids.size() > 0) {
            List<PermissionFile> list = list(new QueryWrapper<PermissionFile>()
                    .lambda()
                    .select(PermissionFile::getFileId)
                    .in(PermissionFile::getPermissionId, ids));
            if (list != null && list.size() != 0) {
                List<Long> fileIds = new ArrayList<>();
                for (PermissionFile permissionFile : list) {
                    fileIds.add(permissionFile.getFileId());
                }
                return fileIds;
            }
        }
        return null;
    }

    @Override
    public List<Long> fileIdsByPermissionId(long permissionId) {
        List<PermissionFile> list = list(new QueryWrapper<PermissionFile>()
                .lambda()
                .select(PermissionFile::getFileId)
                .eq(PermissionFile::getPermissionId,permissionId));
        if (list != null && list.size() != 0) {
            List<Long> fileIds = new ArrayList<>();
            for (PermissionFile permissionFile : list) {
                fileIds.add(permissionFile.getFileId());
            }
            return fileIds;
        }
        return null;
    }

    @Override
    public boolean deletePermissionFileByPermissionId(long permissionId) {
        return remove(new QueryWrapper<PermissionFile>()
                .lambda()
                .eq(PermissionFile::getPermissionId, permissionId)
        );
    }
}
