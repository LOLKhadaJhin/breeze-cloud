package com.nhwb.breeze.controller;

import com.nhwb.breeze.config.util.BeanRefresh;
import com.nhwb.breeze.domain.Permission;
import com.nhwb.breeze.domain.PermissionFile;
import com.nhwb.breeze.service.FilePathService;
import com.nhwb.breeze.service.PermissionFileService;
import com.nhwb.breeze.service.PermissionService;
import com.nhwb.breeze.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

//缺少内部安检，只有js效验和入口root检查，管理员没有必要搞自己，所以没有其它安检，主要是自己懒得写
@Controller
@RequestMapping("/admin")
public class AdminPermissionController {
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PermissionFileService permissionFileService;
    @Autowired
    private BeanRefresh refresh;
    @Autowired
    private FilePathService filePathService;

    @GetMapping("")
    public String home() {
        return "admin";
    }

    @GetMapping("/refresh")
    public String refresh() {
        refresh.refreshAuthorityAll();
        return "redirect:/admin";
    }

    //权限管理
    @GetMapping("/authority")
    public String authority(Model model) {
        model.addAttribute("permissions", permissionService.list());
        return "adminAuthority";
    }

    //添加权限
    @PostMapping("/permission")
    public String permissionSave(String description) {
        Permission permission = new Permission();
        permission.setDescription(description);
        if (permissionService.save(permission)) {
            refresh.refreshAuthorityAll();
        }
        return "redirect:/admin/authority";
    }

    //删除权限
    @DeleteMapping("/permission/{id}")
    public String permissionRemove(@PathVariable Long id) {
        List<Long> fileIds = permissionFileService.fileIdsByPermissionId(id);
        filePathService.deleteByFileIds(fileIds);
        //删除目录
        permissionFileService.deletePermissionFileByPermissionId(id);
        //删除用户权限
        userPermissionService.deleteByPermissionId(id);
        permissionService.removeById(id);
        refresh.refreshAuthorityAll();
        return "redirect:/admin/authority";
    }

    //是否共享给所有人
    @PutMapping("/permission/overt/{id}")
    public String permissionOvert(@PathVariable Long id, boolean choice) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setOvert(choice);
        if (permissionService.updateById(permission)) {
            refresh.refreshAuthorityAll();
        }
        return "redirect:/admin/authority";
    }

    //是否共享给激活用户
    @PutMapping("/permission/activation/{id}")
    public String permissionActivation(@PathVariable Long id, boolean choice) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setActivation(choice);
        if (permissionService.updateById(permission)) {
            refresh.refreshAuthorityAll();
        }
        return "redirect:/admin/authority";
    }

    //查看权限
    @GetMapping("/permission/{id}")
    public String getPermission(@PathVariable String id, Model model) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return "redirect:/admin/authority";
        } else {
            List<PermissionFile> files = permissionFileService.listByPermissionId(permission.getId());
            model.addAttribute("permission", permission);
            model.addAttribute("files", files);
        }
        return "adminPermission";
    }

    //修改权限名称
    @PutMapping("/permission")
    public String updatePermissionName(Permission permission) {
        if (permissionService.updateById(permission)) {
            refresh.refreshAuthorityAll();
        }
        return "redirect:/admin/permission/" + permission.getId();
    }

    //添加该权限文件夹
    @PostMapping("/permissionfile")
    public String addPermissionFile(PermissionFile permissionFile) {
        if (new File(permissionFile.getFile()).isDirectory()) {
            if (permissionFileService.save(permissionFile)) {
                refresh.refreshAuthorityAll();
            }
        } else {
            System.out.println(permissionFile.getFile() + "：这不是一个文件夹");
        }
        return "redirect:/admin/permission/" + permissionFile.getPermissionId();
    }

    //删除权限下文件夹
    @DeleteMapping("/permissionfile/{id}")
    public String deletePermissionFile(@PathVariable Long id, long permissionId) {
        permissionFileService.removeById(id);
        filePathService.deleteByFileId(id);
        refresh.refreshAuthorityAll();
        return "redirect:/admin/permission/" + permissionId;
    }
}
