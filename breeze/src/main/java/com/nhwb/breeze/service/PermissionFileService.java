package com.nhwb.breeze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.PermissionFile;

import java.util.List;


/**
* @author 怒火无边
* @description 针对表【tb_permission_file】的数据库操作Service
* @createDate 2022-04-03 18:42:46
*/
public interface PermissionFileService extends IService<PermissionFile> {
    /**
     *  获取 List<PermissionFile>
     * @param ids 权限IDs
     * @return List<PermissionFile>
     */
    List<PermissionFile> listByPermissionIds(List<Long> ids);

    /**
     * 获取 List<PermissionFile>
     * @param permissionId 权限IDs
     * @return List<PermissionFile>
     */
    List<PermissionFile> listByPermissionId(long permissionId);

    /**
     *  获取 fileIds
     * @param ids 权限IDs
     * @return List<PermissionFile>
     */
    List<Long> fileIdsByPermissionIds(List<Long> ids);
    /**
     *  获取 fileIds
     * @param permissionId 权限ID
     * @return List<PermissionFile>
     */
    List<Long> fileIdsByPermissionId(long permissionId);
    /**
     * 删除该权限ID下所有的文件夹
     * @param permissionId 权限ID
     * @return boolean
     */
    boolean deletePermissionFileByPermissionId(long permissionId);
}
