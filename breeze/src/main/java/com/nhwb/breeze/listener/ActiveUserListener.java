package com.nhwb.breeze.listener;

import com.nhwb.breeze.domain.User;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@WebListener
public class ActiveUserListener implements HttpSessionListener {
    //    private static Integer sessionCount = 0;
    private static final Map<Long, HttpSession> sessionMap = new HashMap<>(); //存放session的集合类
    private static final Map<String, HttpSession> sessionIdMap = new HashMap<>(); //存放session的集合类
    private static final Set<String> sessionSet = new HashSet<>();

    // 当用户与服务器之间开始session时触发该方法
    public void sessionCreated(HttpSessionEvent se) {
        if (se.getSession().isNew()) {
            sessionSet.add(se.getSession().getId());
//            Date date = new Date();
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println("当前时间" + formatter.format(date));
//            System.out.println("当前总在线人数：" + sessionSet.size());
//            System.out.println("所有用户sessionID" + Arrays.toString(sessionSet.toArray()));
//            System.out.println("当前登录用户人数：" + sessionMap.size());
        }
    }

    // 当用户与服务器之间session断开时触发该方法
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        sessionSet.remove(session.getId());
        Object user = session.getAttribute("user");
        if (user != null) {
            sessionMap.remove(((User) user).getId());
            sessionIdMap.remove(session.getId());
        }
//        System.out.println("当前总在线人数：" + sessionSet.size());
//        System.out.println("当前登录用户人数：" + sessionMap.size());
    }

    public static Set<String> getSessionSet() {
        return sessionSet;
    }

    public static Map<Long, HttpSession> getSessionMap() {
        return sessionMap;
    }

    public static Map<String, HttpSession> getStringSessionMap() {
        return sessionIdMap;
    }
}