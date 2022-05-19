package com.nhwb.breeze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nhwb.breeze.domain.UserPermission;
import org.apache.ibatis.annotations.Mapper;


/**
* @author 怒火无边
* @description 针对表【tb_user_permission】的数据库操作Mapper
* @createDate 2022-04-03 18:42:46
* @Entity com.nhwb.breeze.domain.UserPermission
*/
@Mapper
public interface UserPermissionMapper extends BaseMapper<UserPermission> {


}
