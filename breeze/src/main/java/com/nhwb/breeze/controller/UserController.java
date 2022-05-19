package com.nhwb.breeze.controller;

import com.nhwb.breeze.domain.*;
import com.nhwb.breeze.listener.ActiveUserListener;
import com.nhwb.breeze.service.AvoidService;
import com.nhwb.breeze.service.UserPermissionService;
import com.nhwb.breeze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * login 登录
 * logout 退出
 * register 注册
 * /user/save 保存
 * /user/password 修改密码
 */
//@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AvoidService avoidService;
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private BaseConfig baseConfig;

    //登入页面
    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        if (session.getAttribute("class") != null) {
            model.addAttribute("class", "form-text alert alert-danger");
            model.addAttribute("password", "账号或密码错误，如果忘记密码找管理员");
            session.removeAttribute("class");
        }
        if (session.getAttribute("keep") != null) {
            model.addAttribute("keep", "(别搞事情，这个必须点)");
            session.removeAttribute("keep");
        }
        if (session.getAttribute("loginUser") != null) {
            model.addAttribute("loginUser", (UserLogin) session.getAttribute("loginUser"));
            session.removeAttribute("loginUser");
        } else {
            model.addAttribute("loginUser", new UserLogin());
        }
        model.addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        return "login";
    }

    //登入请求
    @PostMapping("/login")
    public String login(UserLogin loginUser, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        if (loginUser == null) {
            return "redirect:/login";
        }
        if (!loginUser.getKeep()) {
            session.setAttribute("keep", "(别搞事情，这个必须点)");
        } else if (loginUser.getUsername() != null && loginUser.getPassword() != null) {
            User user = userService.getUserbyUser(loginUser.getUsername().toLowerCase(), loginUser.getPassword());
            if (user != null) {
                if (user.getActivation()) {
                    user.setGrantPermissionIds(userPermissionService.permissionIdsByUserId(user.getId()));
                }
                user.setPassword(null);
                session.setAttribute("user", user);
                if (baseConfig.getTimeout() != null) {
                    if (baseConfig.getTimeout() >= 0 || baseConfig.getTimeout() <= 800) {
                        session.setMaxInactiveInterval(60 * baseConfig.getTimeout());
                    }
                }
                ActiveUserListener.getSessionMap().put(user.getId(), session);
                ActiveUserListener.getStringSessionMap().put(session.getId(), session);
                //持久登入
                if (loginUser.getSave()) {
                    Avoid avoid = new Avoid();
                    avoid.setUserId(user.getId());
                    avoid.setUuid(UUID.randomUUID().toString());
                    avoid.setAuthorize(true);
                    avoidService.saveOrUpdate(avoid);
                    Cookie userId = new Cookie("userId", avoid.getUserId().toString());
                    Cookie uuid = new Cookie("uuid", avoid.getUuid());
                    userId.setMaxAge(60 * 60 * 24 * 30);
                    uuid.setMaxAge(60 * 60 * 24 * 30);
                    response.addCookie(userId);
                    response.addCookie(uuid);
                }
                return "redirect:/";
            } else {
                session.setAttribute("class", "form-text alert alert-danger");
            }
        }
        session.setAttribute("loginUser", loginUser);
        return "redirect:/login";
    }

    //退出
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.removeAttribute("user");
            Cookie userId = new Cookie("userId", "");
            Cookie uuid = new Cookie("uuid", "");
            userId.setMaxAge(0);
            uuid.setMaxAge(0);
            response.addCookie(userId);
            response.addCookie(uuid);
            Avoid avoid = new Avoid();
            avoid.setUserId(user.getId());
            avoid.setAuthorize(false);
            avoid.setUuid(UUID.randomUUID().toString());
            avoidService.saveOrUpdate(avoid);
        }
        return "redirect:/";
    }

    //注册页面
    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        if (!baseConfig.getRegister()) {
            model.addAttribute("text", "当前游客不允许注册，可以尝试联系管理员")
                    .addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
            return "a";
        }
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        Map errors = (Map) session.getAttribute("errors");
        if (errors != null) {
            UserRegister register = (UserRegister) session.getAttribute("register");
            model.addAttribute("errors", errors);
            model.addAttribute("register", register);
            session.removeAttribute("errors");
            session.removeAttribute("register");
        } else {
            model.addAttribute("errors", new HashMap<String, String>());
            model.addAttribute("register", new UserRegister());
        }
        model.addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        return "register";
    }

    //账号注册
    @PostMapping("/user/save")
    public String save(@Validated UserRegister register, BindingResult bindingResult, HttpSession session) {
        if (!baseConfig.getRegister()) {
            return "redirect:/register";
        }
        Map<String, String> errors = null;
        if (bindingResult.hasErrors()) {
            errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }

        if (errors != null) {
            session.setAttribute("errors", errors);
            session.setAttribute("register", register);
            return "redirect:/register";
        }
        errors = new HashMap<>();
        if (!register.getPassword().equals(register.getAgain())) {
            errors.put("again", "两次密码不一致");
        }
        if (queryUsername(register.getUsername().toLowerCase())) {
            errors.put("username", "该用户名已存在");
        }
        if (userService.existTableFieldValue(User::getFullname, register.getFullname())) {
            errors.put("fullname", "该昵称已存在");
        }
        if (errors.size() > 0) {
            session.setAttribute("errors", errors);
            session.setAttribute("register", register);
            return "redirect:/register";
        }
        User user = new User();
        user.setUsername(register.getUsername().toLowerCase());
        user.setActivation(baseConfig.getActivation());
        user.setFullname(register.getFullname());
        user.setPassword(BCrypt.hashpw(register.getPassword(), BCrypt.gensalt()));
        if (userService.save(user)) {
            return "redirect:/login";
        } else {
            return "redirect:/error/busy";
        }
    }

    @PostMapping("/user/username")
    @ResponseBody
    public boolean queryUsername(String username) {
        return userService.existTableFieldValue(User::getUsername, username.toLowerCase());
    }

    @PostMapping("/user/fullname")
    @ResponseBody
    public boolean queryFullName(String fullname) {
        return userService.existTableFieldValue(User::getFullname, fullname);
    }

    @GetMapping("/user/modify")
    public String modify(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("welcome", "欢迎，" + user.getFullname() + "。");
        }
        Map errors = (Map) session.getAttribute("errors");
        if (errors != null) {
            UserModify userModify = (UserModify) session.getAttribute("userModify");
            model.addAttribute("errors", errors);
            model.addAttribute("userModify", userModify);
            session.removeAttribute("errors");
            session.removeAttribute("register");
        } else {
            model.addAttribute("errors", new HashMap<>());
            model.addAttribute("userModify", new UserModify());
        }
        String visibility = (String) session.getAttribute("visibility");
        if (visibility != null) {
            model.addAttribute("visibility", "alert alert-primary position-absolute top-0 start-50 translate-middle rounded-3");
            session.removeAttribute("visibility");
        } else {
            model.addAttribute("visibility", "invisible");
        }
        model.addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        return "modify";
    }

    //账号密码修改
    @PutMapping("/user/password")
    public String modifyPassword(@Validated UserModify userModify, BindingResult bindingResult, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, String> errors;
        if (user == null) {
            return "redirect:/login";
        } else if (bindingResult.hasErrors()) {
            errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            session.setAttribute("errors", errors);
            session.setAttribute("userModify", userModify);
        } else if (!userModify.getPassword().equals(userModify.getAgain())) {
            errors = new HashMap<>();
            errors.put("again", "两次密码不一致");
            session.setAttribute("errors", errors);
            session.setAttribute("userModify", userModify);
        } else if (userService.modifyUserByUser(user.getId(), userModify.getOld(), userModify.getPassword())) {
            session.setAttribute("visibility", "visibility");
        } else {
            errors = new HashMap<>();
            errors.put("old", "密码不正确");
            session.setAttribute("errors", errors);
            session.setAttribute("userModify", userModify);
        }
        return "redirect:/user/modify";
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
    }
}
