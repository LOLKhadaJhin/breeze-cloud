package com.nhwb.breeze.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheBean {
    //公开文件
    @Bean("overtCache")
    public Map<Long, Cache<String, String>> getOvertCache() {
        return new HashMap<>();
    }
    //激活文件
    @Bean("activationCache")
    public Map<Long, Cache<String, String>> getActivationCache() {
        return new HashMap<>();
    }
    //授权文件
    @Bean("grantCache")
    public Map<Long, Cache<String, String>> getGrantCache() {
        return new HashMap<>();
    }

}
