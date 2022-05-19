package com.nhwb.breeze.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.User;


/**
 * @author 怒火无边
 * @description 针对表【tb_user】的数据库操作Service
 * @createDate 2022-04-03 18:42:46
 */
public interface UserService extends IService<User> {
    /**
     * 获取用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    User getUserbyUser(String username, String password);

    /**
     * 修改密码
     *
     * @param userId  用户Id
     * @param old   旧密码
     * @param young 新密码
     * @return 是否成功
     */
    boolean modifyUserByUser(Long userId, String old, String young);


    /**
     * 重置用户密码为123456
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean resetPasswordById(Long id);

    /**
     * 查询该字段值是否存在
     *
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @return 是否存在
     */
    boolean existTableFieldValue(SFunction<User, ?> fieldName, String fieldValue);
}
