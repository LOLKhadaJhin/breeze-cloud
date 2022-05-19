package com.nhwb.breeze.config;

import com.nhwb.breeze.domain.Avoid;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.listener.ActiveUserListener;
import com.nhwb.breeze.service.AvoidService;
import com.nhwb.breeze.service.UserPermissionService;
import com.nhwb.breeze.service.UserService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器规则
 * 作者：B站「怒火无边」
 */
@Order(-1)
@Component
public class LoginAuthorizeFilter implements HandlerInterceptor {
    private final UserService userService;
    private final AvoidService avoidService;
    private final UserPermissionService userPermissionService;

    public LoginAuthorizeFilter(UserService userService, AvoidService avoidService, UserPermissionService userPermissionService) {
        this.userService = userService;
        this.avoidService = avoidService;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return true;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 1) {
            Cookie userId = null;
            Cookie uuid = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    userId = cookie;
                } else if (cookie.getName().equals("uuid")) {
                    uuid = cookie;
                }
            }
            if (userId != null && uuid != null) {
                Avoid avoid = new Avoid();
                avoid.setUserId(Long.valueOf(userId.getValue()));
                avoid.setUuid(uuid.getValue());
                avoid = avoidService.getAvoidByAvoid(avoid);
                if (avoid != null) {
                    user = userService.getById(avoid.getUserId());
                    if (user != null) {
                        user.setGrantPermissionIds(userPermissionService.permissionIdsByUserId(user.getId()));
                        user.setPassword(null);
                        session.setAttribute("user", user);
                        ActiveUserListener.getSessionMap().put(user.getId(), session);
                        ActiveUserListener.getStringSessionMap().put(session.getId(), session);
//                        avoid.setUuid(UUID.randomUUID().toString());
//                        avoidService.save(avoid);
//                        uuid.setValue(avoid.getUuid());
//                        userId.setMaxAge(60 * 60 * 24 * 30);
//                        uuid.setMaxAge(60 * 60 * 24 * 30);
//                        userId.setPath(request.getContextPath() + "/");
//                        uuid.setPath(request.getContextPath() + "/");
//                        response.addCookie(userId);
//                        response.addCookie(uuid);
                    }
                } else {
                    for (Cookie cookie : cookies) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return true;
    }
}