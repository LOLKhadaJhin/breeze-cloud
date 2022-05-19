package com.nhwb.breeze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.UserPermission;
import com.nhwb.breeze.mapper.UserPermissionMapper;
import com.nhwb.breeze.service.UserPermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 怒火无边
 * @description 针对表【tb_user_permission】的数据库操作Service实现
 * @createDate 2022-04-03 18:42:46
 */
@Service
public class UserPermissionServiceImpl extends ServiceImpl<UserPermissionMapper, UserPermission> implements UserPermissionService {

    @Override
    public List<Long> permissionIdsByUserId(long userId) {
        List<Long> permissionIds = new ArrayList<>();
        List<UserPermission> userPermissions = list(
                new QueryWrapper<UserPermission>()
                        .lambda()
                        .select(UserPermission::getPermissionId, UserPermission::getAuthorize)
                        .eq(UserPermission::getUserId, userId)
        );
        if (userPermissions != null && userPermissions.size() > 0) {
            userPermissions.forEach(v -> {
                if (v.getAuthorize() != null && v.getAuthorize()) {
                    permissionIds.add(v.getPermissionId());
                }
            });
        }
        return permissionIds;
    }

    @Override
    public boolean deleteByUserPermission(UserPermission userPermission) {
        return remove(
                new QueryWrapper<UserPermission>()
                        .lambda()
                        .eq(UserPermission::getUserId, userPermission.getUserId())
                        .eq(UserPermission::getPermissionId, userPermission.getPermissionId())
        );
    }

    @Override
    public boolean deleteByPermissionId(long permissionId) {
        return remove(
                new QueryWrapper<UserPermission>()
                        .lambda()
                        .eq(UserPermission::getPermissionId, permissionId)
        );
    }

    @Override
    public boolean deleteByUserId(long userId) {
        return remove(
                new QueryWrapper<UserPermission>()
                        .lambda()
                        .eq(UserPermission::getUserId, userId)
        );
    }

    @Override
    public UserPermission getUserPermissionByUserPermission(UserPermission userPermission) {
        return getOne(new QueryWrapper<UserPermission>()
                .lambda()
                .eq(UserPermission::getUserId, userPermission.getUserId())
                .eq(UserPermission::getPermissionId, userPermission.getPermissionId())
        );
    }

    @Override
    public boolean addOrModifyUserPermissionByUserPermission(UserPermission userPermission) {
        if (getUserPermissionByUserPermission(userPermission) == null) {
            return save(userPermission);
        } else {
            return update(new UpdateWrapper<UserPermission>()
                    .lambda()
                    .set(UserPermission::getAuthorize, userPermission.getAuthorize())
                    .eq(UserPermission::getUserId, userPermission.getUserId())
                    .eq(UserPermission::getPermissionId, userPermission.getPermissionId())
            );
        }
    }

}
