package com.nhwb.breeze.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.service.AvoidService;
import com.nhwb.breeze.service.FilePathService;
import com.nhwb.breeze.service.UserPermissionService;
import com.nhwb.breeze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * 拦截器
 * 作者：B站「怒火无边」
 */
//@Order(-1)
@Component
public class WebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    private AvoidService avoidService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private Map<String, Map<Long, Map<Long, String>>> filesMap;
    //Cache<FileID, Map<md5, filePath>>
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
    private FilePathService filePathService;
    @Autowired
    private BaseConfig baseConfig;

    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new LoginAuthorizeFilter(userService, avoidService, userPermissionService))
                .addPathPatterns("/**")
                .excludePathPatterns("/error/**", "/file/**", "/user/username", "/user/fullname", "/static/**", "/favicon.ico");
        registry
                .addInterceptor(new LimitAuthorizeFilter(filesMap, baseConfig, overtCache, activationCache, grantCache, filePathService))
                .addPathPatterns("/file/**");
        registry
                .addInterceptor(new AdminAuthorizeFilter())
                .addPathPatterns("/admin/**");
    }
}