package com.nhwb.breeze.controller;


import com.nhwb.breeze.domain.BaseConfig;
import com.nhwb.breeze.domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


@Controller
public class UploadController {
    @Autowired
    private BaseConfig baseConfig;
    private final static String utf8 = "utf-8";

    @PostMapping("/file/{limit}/{permissionId}/{fileId}/{md5}/upload")
    @ResponseBody
    public void upload(@PathVariable("limit") String limit,
                       @PathVariable("permissionId") Long permissionId,
                       @PathVariable("fileId") Long fileId,
                       @PathVariable("md5") String md5,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (!baseConfig.getUpload() || user == null || !user.getUpload()) {
            return;
        }
        String uploadPath = (String) request.getAttribute("path");
        //缓存文件夹
        String repository = baseConfig.getRepository();
        if (!new File(repository).isDirectory()) {
            return;
        }
        //分片
        response.setCharacterEncoding(utf8);
        Integer schunk = null;
        Integer schunks = null;
        String name = null;
        BufferedOutputStream os = null;
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            factory.setRepository(new File(repository));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(5L * 1024L * 1024L * 1024L);
            upload.setSizeMax(10L * 1024L * 1024L * 1024L);
            List<FileItem> items = upload.parseRequest(request);

            for (FileItem item : items) {
                if (item.isFormField()) {
                    if ("chunk".equals(item.getFieldName())) {
                        schunk = Integer.parseInt(item.getString(utf8));
                    }
                    if ("chunks".equals(item.getFieldName())) {
                        schunks = Integer.parseInt(item.getString(utf8));
                    }
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString(utf8);
                    }
                }
            }

            //.....................................................
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String temFileName = name;
                    if (name != null) {
                        File temFile;
                        if (schunk != null) {
                            temFileName = schunk + "_" + name;
                            temFile = new File(repository, temFileName);
                        } else {
                            temFile = new File(uploadPath, temFileName);
                        }
                        if (!temFile.exists()) {//断点续传
                            item.write(temFile);
                        }
                    }
                }
            }
            //文件合并

            if (schunk != null && schunks != null && schunk == schunks - 1) {
                File tempFile = new File(uploadPath, name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));

                for (int i = 0; i < schunks; i++) {
                    File file = new File(repository, i + "_" + name);
                    while (!file.exists()) {
                        Thread.sleep(100);
                    }
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    os.write(bytes);
                    os.flush();
                    file.delete();
                }
                os.flush();
            }
            response.getWriter().write("上传成功" + name);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
