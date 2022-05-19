package com.nhwb.breeze.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.mapper.UserMapper;
import com.nhwb.breeze.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * @author 怒火无边
 * @description 针对表【tb_user】的数据库操作Service实现
 * @createDate 2022-04-03 18:42:46
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    //String hashpw = BCrypt.hashpw("secret", BCrypt.gensalt());
    //boolean checkpw = BCrypt.checkpw(loginUser.getPassword(), user.getPassword());
    @Override
    public User getUserbyUser(String username, String password) {
        User user = getOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUsername, username));
//        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
//            user.setPassword(null);
//            return user;
//        }
        return (user != null && BCrypt.checkpw(password, user.getPassword())) ? user : null;

    }

    @Override
    public boolean modifyUserByUser(Long userId, String old, String young) {
        User user = getById(userId);
        if (BCrypt.checkpw(old, user.getPassword())) {
            user.setPassword(BCrypt.hashpw(young, BCrypt.gensalt()));
            return updateById(user);
        }
        return false;
    }

    @Override
    public boolean resetPasswordById(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        return updateById(user);
    }

    @Override
    public boolean existTableFieldValue(SFunction<User, ?> fieldName, String fieldValue) {
        return count(new QueryWrapper<User>().lambda().eq(fieldName, fieldValue)) == 1;
    }
}
