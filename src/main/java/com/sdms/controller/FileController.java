package com.sdms.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.sdms.common.lang.Result;
import com.sdms.service.DocumentsService;
import com.sdms.service.EsService;
import com.sdms.service.MinioService;
import com.sdms.util.PdfToJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * <p>
 *  文件上传下载
 * </p>
 *
 * @author LSY
 * @since 2022-04-25
 */
@RestController
@Slf4j
public class FileController {

    @Value("${filepath.pdf.path}")
    private String uploadFilePath;
    @Autowired
    private EsService esService;
    @Autowired
    private PdfToJsonUtil pdfToJsonUtil;
    @Autowired
    private MinioService minioService;
    @Autowired
    private DocumentsService doc;

    @Value("${minio.bucket-name}")
    private String bucketName;

    //实现上传文件功能,pid是前端生成的传递给后端【已废弃，新的方法在AutoFileController,改回来只需要去除路径中的/api】
    @PostMapping("/api/uploadFile/{pid}")
    public Result fileUpload(@PathVariable String pid, @RequestParam("file") MultipartFile file) throws IOException
    {
        //@RequestParam("file")表示在请求参数中查找名为file的参数。MultipartFile file用于接收文件
        if (file.isEmpty()) {
            return Result.fail("空文件！");
        }
        // 文件名
        String fileName = file.getOriginalFilename();//获得文件的原始名称
        String suffixName = fileName.substring(fileName.lastIndexOf("."));//获取文件的后缀名
        log.info("上传文件名称为:{}, 后缀名为:{}!", fileName, suffixName);

        if (!suffixName.equals(".pdf")) {
            return Result.fail("文件类型不符!");
        }
        String newName = pid + ".pdf";
        String pathName = uploadFilePath + "/" + newName;//保存到本地的路径地址和新创建的uuid.pdf
        File fileTempObj = new File(pathName);//创建空白的pdf文件
        // 检测目录是否存在
        if (!fileTempObj.getParentFile().exists()) {
            fileTempObj.getParentFile().mkdirs();
        }
        // 使用文件名称检测文件是否已经存在
        if (fileTempObj.exists()) {
            return Result.fail("文件已经存在!");
        }
        try
        {
            FileUtil.writeBytes(file.getBytes(), fileTempObj);//将前端传送来的二进制数据流写入新创建的pdf文件
        } catch (Exception e) {
            log.error("发生错误: {}", e);
            return Result.fail(e.getMessage());
        }

        // 对文件进行处理,将pdf处理为txt文件,再调用python脚本进行正则化匹配,返回json格式数据,json格式的文件也可以保存一下,之前保存的只是纯txt文本
        JSONObject obj = pdfToJsonUtil.parsePdf(pathName, pid);
        //其实可以直接对 JSONObject obj = pdfToJsonUtil.parsePdf(pathName, pid)中的obj进行操作。
        if (doc.save(pdfToJsonUtil.createDocument(obj, pid)))
        //pdfToJsonUtil.createDocument()来解析json对象，在总的json文件中获取mysql所需的字段保存到Documents实例中,再将实体类实例写入mysql中
        {
            if (esService.parseObject(obj, pid))
            //esService.parseObject()解析json对象,将文本根据chapter分成一段段,最后一次性提交,在函数中直接将内容写入es的sdms索引中
            {
                return Result.success("解析文件成功！pid:" + pid);//返回给前端的内容
            }
        }
        return Result.fail("文件解析失败！");
    }
    //实现上传文件功能,pid是前端生成的传递给后端
    @PostMapping("/uploadFile1/{pid}")
    public Result fileUpload2(@PathVariable String pid, @RequestParam("file") MultipartFile file) throws IOException
    {
        //@RequestParam("file")表示在请求参数中查找名为file的参数。MultipartFile file用于接收文件
        if (file.isEmpty()) {
            return Result.fail("空文件！");
        }
        // 文件名
        String fileName = file.getOriginalFilename();//获得文件的原始名称
        String suffixName = fileName.substring(fileName.lastIndexOf("."));//获取文件的后缀名
        log.info("上传文件名称为:{}, 后缀名为:{}!", fileName, suffixName);

        if (!suffixName.equals(".pdf")) {
            return Result.fail("文件类型不符!");
        }
        String newName = pid + ".pdf";
        String pathName = uploadFilePath + "/" + newName;//保存到本地的路径地址和新创建的uuid.pdf
        File fileTempObj = new File(pathName);//创建空白的pdf文件
        // 检测目录是否存在
        if (!fileTempObj.getParentFile().exists()) {
            fileTempObj.getParentFile().mkdirs();
        }
        // 使用文件名称检测文件是否已经存在
        if (fileTempObj.exists()) {
            return Result.fail("文件已经存在!");
        }
        try
        {
            FileUtil.writeBytes(file.getBytes(), fileTempObj);//将前端传送来的二进制数据流写入新创建的pdf文件
        } catch (Exception e) {
            log.error("发生错误: {}", e);
            return Result.fail(e.getMessage());
        }
        // 对文件进行处理,将pdf处理为txt文件,再调用python脚本进行正则化匹配,返回json格式数据,json格式的文件也可以保存一下,之前保存的只是纯txt文本
        JSONObject obj = pdfToJsonUtil.parsePdf(pathName, pid);
        //其实可以直接对 JSONObject obj = pdfToJsonUtil.parsePdf(pathName, pid)中的obj进行操作。

        return Result.success(obj);//返回给前端的内容
    }

    /**
     * 多个文件上传
     *
     * @param files
     * @return
     * @throws JSONException
     */
    @ResponseBody
    @PostMapping("/uploadFiles")
    public String fileUploads(@RequestParam("files") MultipartFile files[]) throws JSONException {
        JSONObject result = new JSONObject();

        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getOriginalFilename();
            File dest = new File(uploadFilePath + '/' + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                files[i].transferTo(dest);
            } catch (Exception e) {
                log.error("发生错误: {}", e);
                result.put("error", e.getMessage());
                return result.toString();
            }
        }
        result.put("success", "文件上传成功!");
        return result.toString();
    }

    /**
     * 多个文件上传
     *
     */
    @ResponseBody
    @PostMapping("/uploadFiles02")
    public String fileUploads(String name, @RequestParam("files") MultipartFile[] files) throws JSONException {
        System.out.println(name);
        JSONObject result = new JSONObject();

        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getOriginalFilename();
            File dest = new File(uploadFilePath + '/' + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                files[i].transferTo(dest);
            } catch (Exception e) {
                log.error("发生错误: {}", e);
                result.put("code", 400);
                result.put("error", e.getMessage());
                return result.toString();
            }
        }
        result.put("code", 200);
        result.put("success", "文件上传成功!");
        return result.toString();
    }

    //下载/预览存储在服务器端的pdf文件
    @CrossOrigin
    @GetMapping("/downloadFile")
    public String fileDownload(HttpServletResponse response, @RequestParam("fileName") String fileName) throws JSONException, IOException {

        JSONObject result = new JSONObject();
        // 前端传递文件名,根据文件名查找文件
        File file = new File(uploadFilePath + '/' + fileName);
        if (!file.exists()) {
            result.put("error", "下载文件不存在!");
            return result.toString();
        }

        // 设置HttpServletResponse响应体的格式,触发浏览器的下载行为。
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);// Content-Disposition头，它设置为 attachment; filename="filename.pdf" 来触发浏览器的下载行为。

        // 下载文件
        byte[] readBytes = FileUtil.readBytes(file);//来读取文件的全部内容到 byte[] 数组中。
        OutputStream os = response.getOutputStream();//将文件内容写入该流，从而发送到客户端
        os.write(readBytes);//将文件内容写入到了HttpServletResponse的输出流中
        result.put("success", "下载成功!");
        return result.toString();
    }


    @ResponseBody
    @PostMapping("/deleteFile")
    public String deleteFile(HttpServletResponse response, @RequestParam("fileName") String fileName) throws JSONException {
        JSONObject result = new JSONObject();
        File file = new File(uploadFilePath + '/' + fileName);
        // 判断文件不为null或文件目录存在
        if (!file.exists()) {
            result.put("success", "文件不存在!");
            return result.toString();
        }
        try {
            if (file.isFile()) file.delete();
            else {
                // 文件夹, 需要先删除文件夹下面所有的文件, 然后删除
                for (File temp : file.listFiles()) {
                    temp.delete();
                }
                file.delete();
            }
        } catch (Exception e) {
            log.error("发生错误: {}", e);
            result.put("error", e.getMessage());
            return result.toString();
        }
        result.put("success", "删除成功!");
        return result.toString();
    }
    private static void writeDataToFile(JSONObject data, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        try {
            writer.write(String.valueOf(data));
        } finally {
            // 确保关闭写入流
            if (writer != null) {
                writer.close();
            }
        }
    }

    // 上传文献到minio(独立于文献解析)
    @PostMapping("/api/pdf/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            String fileUrl=minioService.uploadPdf(file);
            result.put("success","上传成功，文件地址：" + fileUrl);
            return result.toString();
        } catch (Exception e) {
            result.put("code", 500);
            result.put("fall", "上传失败：" + e.getMessage());
            return result.toString();
        }
    }
    // minio中的文献临时预览(配合前端的PDF.js实现,前端axios.get('/preview'))
    @GetMapping("/api/pdf/preview")
    public void previewPdf(@RequestParam String fileName, HttpServletResponse response) {
        try (InputStream is=minioService.getPdfStream(fileName))
        {
            // 设置响应头
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);

            // 流式传输文件内容
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("文件获取失败", e);
        }
    }

    // 从minio中下载文件
    @GetMapping("/api/pdf/download")
    public String downloadPdf(@RequestParam String fileName, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        try {
            InputStream is =minioService.downloadFile(fileName);
            // 设置响应头，触发浏览器下载行为
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 流式传输
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            return result.put("success","下载成功").toString();
        } catch (FileNotFoundException e) {
            return result.put("fail","文件不存在").toString();
        } catch (Exception e) {
            return result.put("fail","下载失败").toString();
        }
    }
}
class FileToMultipartFileConverter {

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return new MockMultipartFile(
                    file.getName(),
                    file.getName(),
                    "application/octet-stream",
                    fileInputStream
            );
        }
    }
}

