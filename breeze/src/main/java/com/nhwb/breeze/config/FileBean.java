package com.nhwb.breeze.config;

import com.nhwb.breeze.domain.BaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FileBean {
    final public static String OVERT = "overt";
    final public static String ACTIVATION = "activation";
    final public static String GRANT = "grant";
    final public static String BACKGROUND = "background";
    //用于首页展示
    @Bean
    public Map<String, Map<Long, String>> getOksMap() {
        //oksMap<limit,okList>....okList<PermissionId,Description>
        return new HashMap<>();
    }
    //用于展示次首页
    @Bean
    public Map<String, Map<Long, Map<Long, String>>> getFilesMap() {
        //filesMap<limit,fileMap>....fileMap<PermissionId,Map<FileID,FileName>>
        return new HashMap<>();
    }

    //基础配置
    @Bean
    public BaseConfig getBase() {
        return new BaseConfig();
    }

}
