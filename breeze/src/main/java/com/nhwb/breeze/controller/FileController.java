package com.nhwb.breeze.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.nhwb.breeze.config.FileBean;
import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.domain.FilePath;
import com.nhwb.breeze.domain.PermissionFileResult;
import com.nhwb.breeze.domain.User;
import com.nhwb.breeze.service.FilePathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Controller
//@Scope("prototype")
@RequestMapping("file")
public class FileController {


    //filesMap<limit,fileMap>....fileMap<PermissionId,Map<FileID,FileName>>
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

    //  /范围/权限ID  ---->   用 范围->权限ID 得到该权限下所有文件夹ID 展示对应文件名
    @GetMapping("/{limit}/{permissionId}")
    public String first(@PathVariable("limit") String limit,
                        @PathVariable("permissionId") Long permissionId,
                        Model model) {

        model.addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        //1查询当前所有目录
        //fileMap<PermissionId,Map<FileID,FileName>>
        Map<Long, Map<Long, String>> fileMap = filesMap.get(limit);
        //2展示所有  PermissionId,Map<FileID,FileName>
        Map<Long, String> PermissionIdMap = fileMap.get(permissionId);
        if (PermissionIdMap == null) {
            model.addAttribute("text", "该权限不存在");
            return "a";
        } else if (PermissionIdMap.size() == 0) {
            model.addAttribute("text", "目前该权限下没有文件");
            return "a";
        }
        List<PermissionFileResult> directoryList = new ArrayList<>();
        PermissionIdMap.forEach((k, v) -> {
            File file = new File(v);
            String path = file.getPath();
//            String uuid = UUID.nameUUIDFromBytes(path.getBytes()).toString();
            String md5 = DigestUtils.md5DigestAsHex(path.getBytes(StandardCharsets.UTF_8));
            PermissionFileResult result = new PermissionFileResult();
            result.setMd5(permissionId + "/" + k + "/" + md5);
            result.setFileName(file.getName().length() == 0 ? path : file.getName());
            directoryList.add(result);
            Cache<String, String> cache = getCache(limit, k);
            saveFilePath(cache, k, md5, path);
        });
        //id 作为子链接
        model
                .addAttribute("directoryList", directoryList)
                .addAttribute("upload", false);

        return "second";
    }

    // /范围/权限ID/文件夹id/文件md5
    @GetMapping("/{limit}/{permissionId}/{fileId}/{md5}")
    public String second(@PathVariable("limit") String limit,
                         @PathVariable("permissionId") Long permissionId,
                         @PathVariable("fileId") Long fileId,
                         @PathVariable("md5") String md5,
                         HttpServletRequest request,
                         Model model) {
        //1获取目录
        String filePath = (String) request.getAttribute("path");
        //1查询当前所有目录
        Cache<String, String> cache = getCache(limit, fileId);
        model.addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        if (cache == null) {
            model.addAttribute("text", "该文件夹不存在");
            return "a";
        }
        if (filePath == null) {
            model.addAttribute("text", "没有该文件或目录");
            return "a";
        }

        //2展示所有
        File fileParent = new File(filePath);
        if (!fileParent.isDirectory()) {
            model.addAttribute("text", "这不是文件夹");
            return "a";
        }

        File[] listFiles = fileParent.listFiles();
        List<PermissionFileResult> directoryList = new ArrayList<>();
        List<PermissionFileResult> fileList = new ArrayList<>();
        List<PermissionFileResult> imgList = new ArrayList<>();
        List<PermissionFileResult> videoList = new ArrayList<>();
        String id = request.getSession().getId();
        assert listFiles != null;
        for (File f : listFiles) {
            PermissionFileResult fileResult = new PermissionFileResult();
//            String fileUUID = UUID.nameUUIDFromBytes(f.getPath().getBytes()).toString();
            String FileMd5 = DigestUtils.md5DigestAsHex(f.getPath().getBytes(StandardCharsets.UTF_8));
            fileResult.setFileName(f.getName());
            if (limit.equals(FileBean.OVERT)) {
                fileResult.setMd5(FileMd5);
            } else {
                fileResult.setMd5(FileMd5 + "/" + id);
            }
            if (f.isDirectory()) {
                directoryList.add(fileResult);
            } else if (baseConfig.getDownload()) {
                fileList.add(fileResult);
            } else if (fileType(f.getName()).equals("mp4")) {
                fileResult.setMd5(FileMd5);
                videoList.add(fileResult);
            } else if (equalsValueList(f.getName())) {
                //Files.probeContentType(f.toPath()).startsWith("image")
                imgList.add(fileResult);
            } else {
                fileList.add(fileResult);
            }
            saveFilePath(cache, fileId, FileMd5, f.getPath());
        }
        User user = (User) request.getSession().getAttribute("user");
        if (!baseConfig.getUpload() || user == null) {
            model.addAttribute("upload", false);
        } else {
            model.addAttribute("upload", user.getUpload());
        }

        model.addAttribute("directoryList", directoryList)
                .addAttribute("fileList", fileList)
                .addAttribute("videoList", videoList)
                .addAttribute("imgList", imgList)
        ;
        return "second";
    }

    @GetMapping("/{limit}/{permissionId}/{fileId}/{md5}/video")
    public String video(@PathVariable("limit") String limit,
                        @PathVariable("permissionId") Long permissionId,
                        @PathVariable("fileId") Long fileId,
                        @PathVariable("md5") String md5,
                        HttpServletRequest request,
                        Model model) {
        String filePath = (String) request.getAttribute("path");
        if (filePath != null && new File(filePath).isFile()) {
            model.addAttribute("videoName", new File(filePath).getName())
                    .addAttribute("i", request.getSession().getId());

        }
//        String agent = request.getHeader("user-agent");
        //判断字符串，Edge、Chrome、Safari、Firefox、IE浏览器或其它
//        if (agent.contains("Edge") || agent.contains("Chrome") || agent.contains("Safari") || agent.contains("Firefox")) {
//            System.out.println(1);
//            return "ckplayer";
//        } else {
//            System.out.println(2);
//            return "video";
//        }
        return "ckplayer";
    }

    @GetMapping({"/{limit}/{permissionId}/{fileId}/{md5}/download",
            "/{limit}/{permissionId}/{fileId}/{md5}/{sessionId}/download"})
    public void download(@PathVariable("limit") String limit,
                         @PathVariable("permissionId") Long permissionId,
                         @PathVariable("fileId") Long fileId,
                         @PathVariable("md5") String md5,
                         @PathVariable(value = "sessionId", required = false) String sessionId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
//        return new FileSystemResource(filePath);
        String filePath = (String) request.getAttribute("path");
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.isFile()) {
            return;
        }
//        if (sessionId != null) {
//            try {
//                String type = Files.probeContentType(file.toPath());
//                if (!((type != null && type.startsWith("video")) || fileType(file.getName()).equals("rmvb"))) {
//                    return;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        long fileLength = file.length();
        long begin = 0;
        Long end = null;
        String range = request.getHeader("Range");//bytes=begin-,多线程bytes=begin-end
        if (range != null && (range = range.trim()).startsWith("b")) {
            response.reset();
            response.setHeader("Accept-Ranges", "bytes");
            // 断点续传
            try {
                if (range.endsWith("-")) {
                    begin = Long.parseLong(range.replaceAll("bytes=", "").replaceAll("-", ""));
                    response.setHeader("Content-Range", "bytes " + begin + "-" + (fileLength - 1) + "/" + fileLength); //contentRange=begin-end/总length
                    response.setHeader("Content-Length", String.valueOf(fileLength - begin));
                } else {
                    String[] split = range.replaceAll("bytes=", "").split("-");
                    begin = Long.parseLong(split[0]);
                    end = Long.parseLong(split[1]);
                    response.setHeader("Content-Range", "bytes " + begin + "-" + end + "/" + fileLength); //contentRange=begin-end/总length
                    response.setHeader("Content-Length", String.valueOf(end - begin + 1));
                }
            } catch (RuntimeException ignored) {
            }
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
        //状态码
        if (range == null) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setHeader("connection", "keep-alive");
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        try {
            //文件类型
            if (fileType(file.getName()).equals("txt")) {
                response.setContentType("text/plain;charset=utf-8");
            } else if (fileType(file.getName()).equals("rmvb")) {
                response.setContentType("video/rmvb");
            } else {
                String type = Files.probeContentType(file.toPath());
                response.setContentType(Objects.requireNonNullElse(type, "application/octet-stream"));//文件类型,字节流
            }
            copy(file, begin, end, response.getOutputStream());
        } catch (IOException ignored) {
        }
    }

    private void copy(File file, long begin, @Nullable Long end, OutputStream outputStream) {
        int n = 0; //每次读取长度
        long readLength = 0;//当前写入长度
        int bytesSize = 1024;  //每次写入长度
        byte[] bytes = new byte[bytesSize];
        try (outputStream; FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            if (begin != 0) {
                bufferedInputStream.skip(begin);//开始的地方
            }
            if (end == null) {
                while ((n = bufferedInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, n);//向outputStream从bytes第0个字节开始输入n个字节
                }
            } else {
                long contentLength = end - begin + 1;//写入长度
                while (true) {
                    n = bufferedInputStream.read(bytes);
                    readLength += n;
                    if (readLength < contentLength) {
                        outputStream.write(bytes, 0, n);//向outputStream从bytes第0个字节开始输入n个字节
                    } else {
                        //超出长度了
                        outputStream.write(bytes, 0, Math.toIntExact(contentLength - (readLength - n)));
                        break;
                    }
                }
            }
            outputStream.flush();
        } catch (IOException ignored) {
        }
    }

    //判断图片
    private boolean equalsValueList(String fileName) {
        String s = fileType(fileName);
        String[] strings = {"jpg", "png", "1.bmp", "1.jpeg", "1.gif", "1.xbm", "1.xpm"};
        boolean flag = false;
        for (String a : strings) {
            if (a.equals(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    //文件类型
    private String fileType(String fileName) {
        String[] split = fileName.split("\\.");
        return split[split.length - 1].toLowerCase();
    }

    //保存文件路径
    private void saveFilePath(Cache<String, String> cache, Long fileId, String md5, String path) {
        if (cache.getIfPresent(md5) == null && filePathService.getById(md5) == null) {
            FilePath filePath = new FilePath();
            filePath.setMd5(md5);
            filePath.setFileId(fileId);
            filePath.setPath(path);
            filePathService.save(filePath);
        }
        cache.put(md5, path);
    }

    private Cache<String, String> getCache(String limit, long fileId) {
        switch (limit) {
            case FileBean.OVERT:
                return overtCache.get(fileId);
            case FileBean.ACTIVATION:
                return activationCache.get(fileId);
            case FileBean.GRANT:
                return grantCache.get(fileId);
            default:
                return null;
        }
    }
}
