package com.sdms.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.sdms.common.lang.Result;
import com.sdms.service.DocumentsService;
import com.sdms.service.EsService;
import com.sdms.util.PdfToJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import static com.sdms.controller.FileToMultipartFileConverter.convertFileToMultipartFile;

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
    private DocumentsService doc;

    //实现上传文件功能,pid是前端生成的传递给后端
    @PostMapping("/uploadFile/{pid}")
    public Result fileUpload(@PathVariable String pid, @RequestParam("file") MultipartFile file) throws IOException
    {
        //@RequestParam("file")表示在请求参数中查找名为file的参数。MultipartFile file用于接收文件
        if (file.isEmpty()) {
            return Result.fail("空文件！");
        }
        // 文件名
        String fileName = file.getOriginalFilename();//获得上传时的文件名称
        String suffixName = fileName.substring(fileName.lastIndexOf("."));//获取文件的后缀名
        log.info("上传文件名称为:{}, 后缀名为:{}!", fileName, suffixName);

        if (!suffixName.equals(".pdf")) {
            return Result.fail("文件类型不符!");
        }
        String newName = pid + ".pdf";
        String pathName = uploadFilePath + "/" + newName;//保存到本地的路径
        File fileTempObj = new File(pathName);//按照上面的路径创建文件
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
             FileUtil.writeBytes(file.getBytes(), fileTempObj);//读取pdf文件到fileTempObj(相当复制为fileTempObj(/uploadFile/newName.pdf))
        } catch (Exception e) {
            log.error("发生错误: {}", e);
            return Result.fail(e.getMessage());
        }
        //实现直接上传文件的功能
        JSONObject obj = pdfToJsonUtil.parsePdf(pathName, pid);//使用tika.py将刚才上传的pdf文件转换成json文本
        String fileName1=uploadFilePath+"/text/"+pid+".txt";//要保存的文件路径
        writeDataToFile(obj, fileName1);//将数据写入到指定的text文件中
        File objs = new File(fileName1);//创建文件
        MultipartFile multipartFile = convertFileToMultipartFile(objs);//将文件转换为MultipartFile格式
        JSONObject object = pdfToJsonUtil.parseJson(multipartFile);//解析json格式文件
        if (doc.save(pdfToJsonUtil.createDocument(object, pid)))
        //pdfToJsonUtil.createDocument()来解析json对象，将元数据取出，返回一个Documents对象使用doc.save()写入mysql中
        {
            if (esService.parseObject(object, pid))
            //!!!esService.parseObject()解析json对象,将文本根据chapter分成一段段,最后一次性提交,在函数中直接将内容写入es的sdms索引中
            // 该功能在点击“文本”按键所显示的内容,必须在在网页上传才能显示内容
            {
                return Result.success("解析文件成功！pid:" + pid);
            }
        }
        return Result.fail("文件解析失败！");
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


    // 下载到了默认的位置
    @CrossOrigin
    @GetMapping("/downloadFile")
    public String fileDownload(HttpServletResponse response, @RequestParam("fileName") String fileName) throws JSONException, IOException {
        JSONObject result = new JSONObject();

        File file = new File(uploadFilePath + '/' + fileName);
        if (!file.exists()) {
            result.put("error", "下载文件不存在!");
            return result.toString();
        }

        // 清空响应的一些信息，包括全局的跨域配置,注释之
        // response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);


        // 原生的方式
        // try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
        //     byte[] buff = new byte[1024];
        //     OutputStream os = response.getOutputStream();
        //     int i = 0;
        //     while ((i = bis.read(buff)) != -1) {
        //         os.write(buff, 0, i);
        //         os.flush();
        //     }
        // } catch (IOException e) {
        //     log.error("发生错误: {}", e);
        //     result.put("error", e.getMessage());
        //     return result.toString();
        // }
        // 简单方式: 方式1
        // byte[] bytes = FileCopyUtils.copyToByteArray(file);
        // 简单方式: 方式2
        byte[] readBytes = FileUtil.readBytes(file);
        OutputStream os = response.getOutputStream();
        os.write(readBytes);
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

