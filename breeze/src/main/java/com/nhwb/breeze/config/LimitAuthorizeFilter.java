package com.nhwb.breeze.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.domain.FilePath;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.listener.ActiveUserListener;
import com.nhwb.breeze.service.FilePathService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Order(-1)
@Component
public class LimitAuthorizeFilter implements HandlerInterceptor {

    //filesMap<limit,fileMap>....fileMap<PermissionId,Map<FileID,FileName>>
    private final Map<String, Map<Long, Map<Long, String>>> filesMap;
    private final BaseConfig baseConfig;
    //Map<FileID, Cache<md5, filePath>> 减少数据库查询
    private final Map<Long, Cache<String, String>> overtCache;
    private final Map<Long, Cache<String, String>> activationCache;
    private final Map<Long, Cache<String, String>> grantCache;
    private final FilePathService filePathService;

    public LimitAuthorizeFilter(Map<String, Map<Long, Map<Long, String>>> filesMap, BaseConfig baseConfig, Map<Long, Cache<String, String>> overtCache, Map<Long, Cache<String, String>> activationCache, Map<Long, Cache<String, String>> grantCache, FilePathService filePathService) {
        this.filesMap = filesMap;
        this.baseConfig = baseConfig;
        this.overtCache = overtCache;
        this.activationCache = activationCache;
        this.grantCache = grantCache;
        this.filePathService = filePathService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String limit = (String) pathVariables.get("limit");
        Long permissionId = Long.valueOf((String) pathVariables.get("permissionId"));
        if (limit.equals(FileBean.BACKGROUND)) {
            String md5 = (String) pathVariables.get("md5");
            if (md5 != null) {
                String path = baseConfig.getBackgroundMd5().get(md5);
                request.setAttribute("path", path);
                return true;
            }
            return false;
        } else if (limit.equals(FileBean.GRANT) || limit.equals(FileBean.ACTIVATION)) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                String sessionId = (String) pathVariables.get("sessionId");
                if (sessionId != null) {
                    HttpSession httpSession = ActiveUserListener.getStringSessionMap().get(sessionId);
                    if (httpSession != null) {
                        user = (User) httpSession.getAttribute("user");
                    }
                }
                if (user == null) {
                    request.setAttribute("limit", "请先登入");
                    response.sendRedirect("/error/limit");
                    return false;
                }
            }
            //授权文件
            if (limit.equals(FileBean.GRANT)) {
                //效验角色权限
                if (!user.getUsername().equals("root") && !user.getGrantPermissionIds().contains(permissionId)) {
                    request.setAttribute("limit", "权限不足，需要更多权限请联系管理员");
                    response.sendRedirect("/error/limit");
                    return false;
                }
            }
            //激活文件
            if (limit.equals(FileBean.ACTIVATION)) {
                //效验角色激活
                if (!user.getUsername().equals("root") && !user.getActivation()) {
                    request.setAttribute("limit", "账号未激活，请联系管理员激活");
                    response.sendRedirect("/error/limit");
                    return false;
                }
            }
        } else if (!limit.equals(FileBean.OVERT)) {
            request.setAttribute("limit", "访问路径错误");
            response.sendRedirect("/error/limit");
            return false;
        }

        String fileId1 = (String) pathVariables.get("fileId");

        long fileId;
        if (fileId1 != null) {
            //查看文件
            fileId = Long.parseLong(fileId1);
        } else {
            //到这里查看某权限
            return true;
        }
        //第一个key
        //fileMap<PermissionId,Map<FileID,FileName>>
        Map<Long, Map<Long, String>> fileMap = filesMap.get(limit);
        //第二个key
        //2展示所有  PermissionIdMap<FileID,FileName>
        Map<Long, String> PermissionId = fileMap.get(permissionId);
        String md5 = (String) pathVariables.get("md5");
        if (PermissionId != null && PermissionId.get(fileId) != null && md5 != null) {
            Map<Long, Cache<String, String>> authorityMap = null;
            switch (limit) {
                case FileBean.OVERT:
                    authorityMap = overtCache;
                    break;
                case FileBean.ACTIVATION:
                    authorityMap = activationCache;
                    break;
                case FileBean.GRANT:
                    authorityMap = grantCache;
                    break;
            }
            //第三个key
            Cache<String, String> cache = authorityMap.get(fileId);
            //第四个key
            String path = cache.getIfPresent(md5);
            if (path == null) {
                FilePath filePath = filePathService.getById(md5);
                if (filePath != null && filePath.getFileId() == fileId) {
                    path = filePath.getPath();
                }
            }
            //这里用四个key验证+用户信息，一个limit，一个permissionId，一个fileId，一个md5
            if (path != null) {
                cache.put(md5, path);
                request.setAttribute("path", path);
                return true;
            }
        }
        request.setAttribute("limit", "访问路径错误");
        response.sendRedirect("/error/limit");
        return false;
    }
}
