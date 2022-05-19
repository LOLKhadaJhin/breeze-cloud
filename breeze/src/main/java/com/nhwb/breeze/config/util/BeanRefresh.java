package com.nhwb.breeze.config.util;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nhwb.breeze.config.FileBean;
import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.domain.Permission;
import com.nhwb.breeze.domain.PermissionFile;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.listener.ActiveUserListener;
import com.nhwb.breeze.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BeanRefresh {
    //    InitializingBean
    @Autowired
    PermissionService permissionService;
    @Autowired
    PermissionFileService permissionFileService;
    @Autowired
    Map<String, Map<Long, String>> oksMap;
    @Autowired
    Map<String, Map<Long, Map<Long, String>>> filesMap;
    @Autowired
    @Qualifier("overtCache")
    private Map<Long, Cache<String, String>> overtCache;
    @Autowired
    @Qualifier("activationCache")
    private Map<Long, Cache<String, String>> activationCache;
    @Autowired
    @Qualifier("grantCache")
    private Map<Long, Cache<String, String>> grantCache;
    @Autowired
    private BaseConfig baseConfig;
    @Autowired
    private BaseConfigService baseConfigService;
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private UserService userService;


    //查询目录（权限str-ID-目录ID:目录）
    //刷新对应缓存目录
    public void refresh(String limit) {
        switch (limit) {
            case FileBean.OVERT:
                intoFileOvert();
                break;
            case FileBean.ACTIVATION:
                intoFileActivation();
                break;
            case FileBean.GRANT:
                intoFileGrant();
                break;
        }
    }

    //刷新全部权限
    public void refreshAuthorityAll() {
        oksMap.clear();
        filesMap.clear();
        intoFileOvert();
        intoFileActivation();
        intoFileGrant();
    }

    //共享所有人
    private void intoFileOvert() {
        List<Permission> list = permissionService.idAndDescriptionByOvert(true);
        browseFile(FileBean.OVERT, list, overtCache);
    }

    //共享激活用户
    private void intoFileActivation() {
        List<Permission> list = permissionService.idAndDescriptionByOvertAndActivation(false, true);
        browseFile(FileBean.ACTIVATION, list, activationCache);
    }

    //授权
    private void intoFileGrant() {
        List<Permission> list = permissionService.idAndDescriptionByOvertAndActivation(false, false);
        browseFile(FileBean.GRANT, list, grantCache);
    }

    //工具类，根据要求查询目录
    private void browseFile(String limit, List<Permission> permissions, Map<Long, Cache<String, String>> cacheMap) {
        cacheMap.clear();
        //oksMap<limit,okList>....okList<PermissionId,Description>
        Map<Long, String> okList = new HashMap<>();
        //filesMap<limit,fileMap>....fileMap<PermissionId,Map<FileID,FileName>>
        Map<Long, Map<Long, String>> fileMap = new HashMap<>();
        //-----------------okList------------------------
        if (permissions != null && permissions.size() > 0) {
            List<Long> ids = new ArrayList<>();
            permissions.forEach(v -> {
                ids.add(v.getId());
                fileMap.put(v.getId(), new HashMap<>());
                okList.put(v.getId(), v.getDescription());
            });
            //--------------------fileMap----------------------
            List<PermissionFile> files = permissionFileService.listByPermissionIds(ids);
            if (files != null && files.size() > 0) {
                files.forEach(v -> {
                    if (new File(v.getFile()).isDirectory()) {
                        Map<Long, String> longStringMap = fileMap.get(v.getPermissionId());
                        longStringMap.put(v.getFileId(), v.getFile());
                        cacheMap.put(v.getFileId(), Caffeine.newBuilder()
                                .initialCapacity(88)
                                .maximumSize(8888)
                                .expireAfterWrite(Duration.ofDays(3))
                                .build());
                    } else {
                        System.out.println(v.getFile() + "不是目录或不存在");
                    }
                });
            }
        }
        oksMap.put(limit, okList);
        filesMap.put(limit, fileMap);
    }

    //刷新admin
    public void refreshBaseConfig() {
        BaseConfig base = baseConfigService.getById(1);
        if (base == null) {
            base = new BaseConfig();
            base.setUserId(1L);
            baseConfigService.save(base);
            base = baseConfigService.getById(1);
        }
        //缓存文件夹
        if (base.getRepository() != null) {
            File file = new File(base.getRepository());
            if (!file.isDirectory()) {
                if (!file.mkdirs()) {
                    System.out.println("上传缓存目录无效，请重新添加！");
                    base.setRepository(null);
                    baseConfigService.update(new UpdateWrapper<BaseConfig>().lambda().set(BaseConfig::getRepository, null).eq(BaseConfig::getUserId, 1L));
                }
            }
        }
        if (base.getRepository() == null) {
            System.out.println("上传功能无法开启！需要设置缓存目录");
            if (base.getUpload()) {
                base.setUpload(false);
                baseConfigService.save(base);
            }
        }

        //背景图片
        Map<String, String> md5 = new HashMap<>();
        if (base.getBackgroundDirectory() != null) {
            File file = new File(base.getBackgroundDirectory());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        try {
                            String type = Files.probeContentType(f.toPath());
                            if (type != null && type.startsWith("image")) {
//                                uuid.put(UUID.nameUUIDFromBytes(f.getPath().getBytes()).toString(), f.getPath());
                                md5.put(DigestUtils.md5DigestAsHex(f.getPath().getBytes(StandardCharsets.UTF_8)), f.getPath());
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }
            } else {
                base.setBackgroundDirectory(null);
                baseConfigService.update(new UpdateWrapper<BaseConfig>().lambda().set(BaseConfig::getBackgroundDirectory, null).eq(BaseConfig::getUserId, 1L));
            }
        }
        base.setBackgroundMd5(md5);
        BeanUtils.copyProperties(base, baseConfig);
    }


    //刷新用户
    public void refreshUser(long userId) {
        HttpSession session = ActiveUserListener.getSessionMap().get(userId);
        if (session != null) {
            User user = userService.getById(userId);
            if (user != null) {
                if (user.getActivation()) {
                    user.setGrantPermissionIds(userPermissionService.permissionIdsByUserId(user.getId()));
                }
                user.setPassword(null);
                session.setAttribute("user", user);
            } else {
                session.removeAttribute("user");
            }
        }
    }
}
