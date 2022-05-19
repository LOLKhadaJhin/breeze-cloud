package com.nhwb.breeze.config;

import com.nhwb.breeze.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminAuthorizeFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            response.sendRedirect("/login");
        }else if (user.getUsername().equals("root")){
            return true;
        }else {
            request.setAttribute("limit", "涉密重地，禁止入内！");
            response.sendRedirect("/error/limit");
        }
        return false;
    }
}
