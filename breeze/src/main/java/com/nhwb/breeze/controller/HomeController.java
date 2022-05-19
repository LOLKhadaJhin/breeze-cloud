package com.nhwb.breeze.controller;

import com.nhwb.breeze.config.FileBean;
import com.nhwb.breeze.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private Map<String, Map<Long, String>> oksMap;

    //首页
    @GetMapping({"/"})
    public String home(HttpSession session, Model model) {
        model.addAttribute("title", "微风网盘")
                .addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        Map<Long, String> okOvert = oksMap.get(FileBean.OVERT);
        model.addAttribute(FileBean.OVERT, okOvert);
        //激活用户
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user.getUsername());
            if (user.getUsername().equals("root") || user.getActivation()) {
                //展示注册激活用户目录
//            Map<Long, String> okActivation = cacheBean.getOkMap(CacheBean.Activation);
                Map<Long, String> okActivation = oksMap.get(FileBean.ACTIVATION);
                model.addAttribute(FileBean.ACTIVATION, okActivation);
                //展示授权目录
//            Map<Long, String> okGrant = cacheBean.getOkMap(CacheBean.Grant);
                Map<Long, String> okGrant = oksMap.get(FileBean.GRANT);
                model.addAttribute("text", "你好," + user.getFullname());
                if (user.getUsername().equals("root")) {
                    model.addAttribute(FileBean.GRANT, okGrant);
                } else {
                    List<Long> grants = user.getGrantPermissionIds();
                    if (grants != null && grants.size() > 0 && okGrant != null) {
                        Map<Long, String> okGrantPart = new HashMap<>();
                        grants.forEach(v -> {
                            String s = okGrant.get(v);
                            if (s != null) {
                                okGrantPart.put(v, s);
                            }
                        });
                        model.addAttribute(FileBean.GRANT, okGrantPart);
                    }
                }
            } else {
                model.addAttribute("text", "你好," + user.getFullname() + "。账号未激活，请联系管理员。");

            }
        }
        return "home";
    }
}
