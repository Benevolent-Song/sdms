package com.sdms.util;

import com.alibaba.fastjson.JSONObject;
import com.sdms.entity.Documents;
import lombok.Data;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static com.sdms.util.MultipartFileToFileUtil.multipartFileToFile;

/**
 * 工具类
 */
@Data
@Component
public class PdfToJsonUtil {

    @Value("${filepath.py.path}")
    private String py;
    @Value("${filepath.txt.path}")
    private String txt;

    // 随机产生一串id
    public String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }

    // 解析pdf，返回一个json对象
    public JSONObject parsePdf(String pdfPath, String pdfName) throws UnsupportedEncodingException {

        String txtPath = txt + pdfName + ".txt";
        try {

            BodyContentHandler handler = new BodyContentHandler(20*1024*1024);
            Metadata metadata = new Metadata();
            FileInputStream inputstream = new FileInputStream(pdfPath);
            ParseContext pcontext = new ParseContext();

            // 通过PDF parser解析文档
            PDFParser pdfparser = new PDFParser();

            pdfparser.parse(inputstream, handler, metadata,pcontext);

            File file = new File(txtPath);
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            BufferedWriter writer=new BufferedWriter(write);
            writer.write(handler.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(py);
        PyFunction function = interpreter.get("pdf2json", PyFunction.class);
        PyObject obj = function.__call__(new PyString(txtPath));
        String jsonString = obj.toString();
        jsonString = URLEncoder.encode(jsonString, "ISO-8859-1");
        String newString = URLDecoder.decode(jsonString, "UTF-8");

        // 删除.txt临时文件
        //FileSystemUtils.deleteRecursively(new File(txtPath));
        return JSONObject.parseObject(newString);
    }

    public JSONObject parseJson(MultipartFile file) {
        BufferedReader reader = null;
        StringBuilder lastStr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(Objects.requireNonNull(multipartFileToFile(file)));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //数据获取
                lastStr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String jsonString = lastStr.toString();

        return JSONObject.parseObject(jsonString);
    }

    // 解析json对象，将元数据取出，返回一个Documents对象
    public Documents createDocument(JSONObject jsonObject, String pid) {

        JSONObject obj = jsonObject.getJSONObject("info");
        Documents doc = new Documents();

        doc.setPid(pid);

        String number = obj.getString("number");
        if(number.length() > 32) {
            number = number.substring(0, 32);
        }
        doc.setNumber(number);

        doc.setCategory(obj.getString("category"));

        String titleCn = obj.getString("titleCn");
        if(titleCn.length() > 255) {
            titleCn = titleCn.substring(0, 255);
        }
        doc.setTitleCn(titleCn);

        String titleEn = obj.getString("titleEn");
        if(titleEn.length() > 512) {
            titleEn = titleEn.substring(0, 512);
        }
        doc.setTitleEn(titleEn);

        doc.setIssuedBy(obj.getString("issuedBy"));

        String releaseDate = obj.getString("releaseDate");
        if(releaseDate.length() > 32) {
            releaseDate = releaseDate.substring(0, 32);
        }
        doc.setReleaseDate(releaseDate);

        String implementDate = obj.getString("implementDate");
        if(implementDate.length() > 32) {
            implementDate = implementDate.substring(0, 32);
        }
        doc.setImplementDate(implementDate);

        doc.setDomain(obj.getString("domain"));

        return doc;
    }

}

