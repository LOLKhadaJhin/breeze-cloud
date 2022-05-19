package com.nhwb.breeze.controller;

import com.nhwb.breeze.config.util.BeanRefresh;
import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.service.BaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminBaseController {
    @Autowired
    private BaseConfig baseConfig;
    @Autowired
    private BaseConfigService baseConfigService;
    @Autowired
    private BeanRefresh refresh;

    @GetMapping("/base")
    public String base(Model model) {
        //刷新图片
        if (baseConfig.getBackgroundDirectory() != null) {
            Map<String, String> backgroundMd5 = baseConfig.getBackgroundMd5();
            File file = new File(baseConfig.getBackgroundDirectory());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        try {
                            String type = Files.probeContentType(f.toPath());
                            if (type != null && type.startsWith("image")) {
//                                backgroundMd5.put(UUID.nameUUIDFromBytes(f.getPath().getBytes()).toString(), f.getPath());
                                backgroundMd5.put(DigestUtils.md5DigestAsHex(f.getPath().getBytes(StandardCharsets.UTF_8)), f.getPath());
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        model.addAttribute("base", baseConfig);
        return "adminBase";
    }

    @PutMapping("/baseconfig")
    public String modifyBaseConfig(BaseConfig base) {
        System.out.println(base);
        boolean flag = true;
        if (base == null) {
            flag = false;
        } else if (base.getRepository() != null) {
            //缓存
            File file = new File(base.getRepository());
            if (!file.isDirectory()) {
                flag = file.mkdirs();
            }
        } else if (base.getBackgroundDirectory() != null) {
            //背景文件夹
            File file = new File(base.getBackgroundDirectory());
            if (!file.isDirectory()) {
                flag = file.mkdirs();
            }
        } else if (base.getBackground() != null) {
            //背景
            if (baseConfig.getBackgroundMd5().get(base.getBackground()) == null && !base.getBackground().equals("吴彦祖")) {
                flag = false;
            }
        } else if (base.getTimeout() != null) {
            if (base.getTimeout() < 0 || base.getTimeout() > 800) {
                flag = false;
            }
        } else if (base.getUpload() == null && base.getActivation() == null
                && base.getDownload() == null && base.getRegister() == null) {
            //哒哒哒哒
            flag = false;
        }
        if (flag) {
            base.setUserId(1L);
            baseConfigService.updateById(base);
            refresh.refreshBaseConfig();
        } else {
            System.out.println(base);
        }
        return "redirect:/admin/base";
    }
}
