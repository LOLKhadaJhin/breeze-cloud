package com.nhwb.breeze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.Permission;

import java.util.List;


/**
 * @author 怒火无边
 * @description 针对表【tb_permission】的数据库操作Service
 * @createDate 2022-04-03 18:42:46
 */
public interface PermissionService extends IService<Permission> {
    /**
     * overt为true时，该资源公开
     *
     * @param overt 公开
     * @return List<Permission>
     */
    List<Permission> idAndDescriptionByOvert(boolean overt);

    /**
     * overt为false时，且，activation为true时向所有激活用户公开，false时需要授权访问
     *
     * @param overt      公开
     * @param activation 激活
     * @return List<Permission>
     */
    List<Permission> idAndDescriptionByOvertAndActivation(boolean overt, boolean activation);
}
