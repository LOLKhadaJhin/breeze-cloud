package com.nhwb.breeze.controller;

import com.nhwb.breeze.config.util.BeanRefresh;
import com.nhwb.breeze.domain.Permission;
import com.nhwb.breeze.domain.PermissionResult;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.domain.UserPermission;
import com.nhwb.breeze.service.AvoidService;
import com.nhwb.breeze.service.PermissionService;
import com.nhwb.breeze.service.UserPermissionService;
import com.nhwb.breeze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AvoidService avoidService;
    @Autowired
    private BeanRefresh refresh;
    //用户管理
    @GetMapping("/users")
    public String authority(Model model) {
        List<User> list = userService.list();
        model.addAttribute("users", list);
        return "adminUsers";
    }

    //用户密码重置
    @PutMapping("/user/password/{userId}")
    public String resetPassword(@PathVariable long userId) {
        userService.resetPasswordById(userId);
        return "redirect:/admin/users";
    }

    //删除用户
    @DeleteMapping("/user/{userId}")
    public String delete(@PathVariable long userId) {
        if (userId != 1L) {
            userService.removeById(userId);
            userPermissionService.deleteByUserId(userId);
            avoidService.removeById(userId);
            refresh.refreshUser(userId);
        }
        return "redirect:/admin/users";
    }


    //激活用户
    //上传权限
    //修改昵称
    @PutMapping("/user")
    public String permissionOvert(User user) {
        if (user.getId() != null && (user.getActivation() != null || user.getUpload() != null)) {
            userService.updateById(user);
            refresh.refreshUser(user.getId());
        } else if (user.getId() != null && user.getFullname() != null) {
            userService.updateById(user);
            refresh.refreshUser(user.getId());
            return "redirect:/admin/user/" + user.getId();
        }
        return "redirect:/admin/users";
    }

    //查看用户
    @GetMapping("/user/{id}")
    public String getPermission(@PathVariable String id, Model model) {
        User user = userService.getById(id);
        if (user == null) {
            return "redirect:/admin/users";
        } else {
            List<Long> permissionIds = userPermissionService.permissionIdsByUserId(user.getId());
            List<Permission> permissionList = permissionService.list();
            ArrayList<PermissionResult> permissionResultList = new ArrayList<>();
            if (permissionList != null && permissionList.size() > 0) {
                permissionList.forEach(v -> {
                    PermissionResult result = new PermissionResult();
                    result.setId(v.getId());
                    result.setDescription(v.getDescription());
                    if (permissionIds.contains(v.getId())) {
                        result.setGrant(true);
                    }
                    permissionResultList.add(result);
                });
            }
            model.addAttribute("user", user);
            model.addAttribute("permissions", permissionResultList);
        }
        return "adminUser";
    }

    //添加授权或修改
    @RequestMapping(value = "/userPermission", method = {RequestMethod.POST, RequestMethod.PUT})
    public String grant(UserPermission userPermission) {
        System.out.println(userPermission);
        if (userPermission != null && userPermission.getUserId() != null && userPermission.getPermissionId() != null) {
            System.out.println(userPermissionService.addOrModifyUserPermissionByUserPermission(userPermission));
            refresh.refreshUser(userPermission.getUserId());
            return "redirect:/admin/user/" + userPermission.getUserId();
        } else {
            return "redirect:/admin/users";
        }
    }


    //删除授权
    @DeleteMapping("/userPermission")
    public String revoke(UserPermission userPermission) {
        if (userPermission != null && userPermission.getUserId() != null && userPermission.getPermissionId() != null) {
            userPermissionService.deleteByUserPermission(userPermission);
            refresh.refreshUser(userPermission.getUserId());
            return "redirect:/admin/user/" + userPermission.getUserId();
        } else {
            return "redirect:/admin/users";
        }
    }

}
