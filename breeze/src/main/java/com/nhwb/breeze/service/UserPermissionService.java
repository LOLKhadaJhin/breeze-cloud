package com.nhwb.breeze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.Permission;
import com.nhwb.breeze.domain.UserPermission;

import java.util.List;


/**
* @author 怒火无边
* @description 针对表【tb_user_permission】的数据库操作Service
* @createDate 2022-04-03 18:42:46
*/
public interface UserPermissionService extends IService<UserPermission> {
    /**
     * 获取该ID用户所有权限ID
     * @param userId 用户id
     * @return 该ID用户所有权限ID
     */
    List<Long> permissionIdsByUserId(long userId);

    /**
     * 删除用户权限
     * @param userPermission 用户权限
     * @return 是否成功
     */
    boolean deleteByUserPermission(UserPermission userPermission);

    /**
     * 删除所有用户该权限
     * @param permissionId 权限ID
     * @return 删除
     */
    boolean deleteByPermissionId(long permissionId);

    /**
     * 删除所有用户该权限
     * @param userId 权限ID
     * @return 删除
     */
    boolean deleteByUserId(long userId);

    /**
     * 查询用户该权限是否存在
     * @param userPermission 用户权限
     * @return 是否成功
     */
    UserPermission getUserPermissionByUserPermission(UserPermission userPermission);
    /**
     * 查询用户该权限是否存在
     * @param userPermission 用户权限
     * @return 是否成功
     */
    boolean addOrModifyUserPermissionByUserPermission(UserPermission userPermission);
}
