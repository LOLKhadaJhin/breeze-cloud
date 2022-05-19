package com.nhwb.breeze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.Permission;
import com.nhwb.breeze.mapper.PermissionMapper;
import com.nhwb.breeze.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 怒火无边
 * @description 针对表【tb_permission】的数据库操作Service实现
 * @createDate 2022-04-03 18:42:46
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {


    @Override
    public List<Permission> idAndDescriptionByOvert(boolean overt) {
        return list(
                new QueryWrapper<Permission>()
                        .lambda()
                        .select(Permission::getId, Permission::getDescription)
                        .eq(Permission::getOvert, overt));
    }

    @Override
    public List<Permission> idAndDescriptionByOvertAndActivation(boolean overt, boolean activation) {

        return list(
                new QueryWrapper<Permission>()
                        .lambda()
                        .select(Permission::getId,Permission::getDescription)
                        .eq(Permission::getOvert, overt)
                        .eq(Permission::getActivation, activation)
        );
    }
}
