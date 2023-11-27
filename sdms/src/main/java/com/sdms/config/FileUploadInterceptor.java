package com.sdms.config;


import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;


@Component
public class FileUploadInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 文件上传的Servlet
        if (request instanceof MultipartHttpServletRequest) {

            // 文件后缀类型
//            String[] disableType = disableFileTypes.split(",");
//            List<String> disableTypeList = Arrays.stream(disableType).collect(Collectors.toList());

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Iterator<String> it = multipartRequest.getFileNames();
            while (it.hasNext()) {
                String fileParameter = it.next();
                List<MultipartFile> listFile = multipartRequest.getFiles(fileParameter);
                if (!CollectionUtils.isEmpty(listFile)) {
                    MultipartFile multipartFile;
                    String fileName;
                    String fileSuffixType = "";

                    for (MultipartFile file : listFile) {
                        // 获取后缀名
                        multipartFile = file;
                        fileName = multipartFile.getOriginalFilename();
                        int indexLocation = 0;
                        if ((indexLocation = fileName.lastIndexOf(".")) > 0) {
                            fileSuffixType = fileName.substring(indexLocation + 1);
                        }


                        // 后缀名检测
                        String enableFileTypes = "pdf";
                        if (!enableFileTypes.equals(fileSuffixType)) {
                            response.setCharacterEncoding("UTF-8");
                            ServletOutputStream outputStream = response.getOutputStream();
                            outputStream.write((fileSuffixType + "是不被允许的上传文件类型!").getBytes());

                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}

