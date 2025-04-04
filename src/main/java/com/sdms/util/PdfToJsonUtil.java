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
    private String txt;//存放临时文件

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

            BodyContentHandler handler = new BodyContentHandler(20*1024*1024);//handler用于存储解析后的文本内容，handler的缓冲区大小
            Metadata metadata = new Metadata();//用于存储PDF文件的元数据。
            FileInputStream inputstream = new FileInputStream(pdfPath);//创建了一个输入流inputstream，用于读取PDF文件。
            ParseContext pcontext = new ParseContext();//Apache Tika解析器的上下文对象，用于传递解析过程中的配置。


            PDFParser pdfparser = new PDFParser();

            //通过tika包下的PDFParser工具类,将PDF文件解析为文本，存储到handler对象中。
            pdfparser.parse(inputstream, handler, metadata,pcontext);

            File file = new File(txtPath);//创建转换为json格式前的txt文件
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);//将pdf文件读取到txt文件中
            BufferedWriter writer=new BufferedWriter(write);//创建了BufferedWriter对象writer，用于写入文本到文件。
            writer.write(handler.toString());//调用writer.write方法，将handler中的文本内容写入到文件中。
            writer.close();//关闭输出流

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 执行python脚本,将txt文件转换为json格式(key-value)
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(py);//用于执行Python代码。
        PyFunction function = interpreter.get("pdf2json", PyFunction.class);//从tika.py中获取名为pdf2json的函数。
        PyObject obj = function.__call__(new PyString(txtPath));//调用pdf2json函数，并将解析后的文本文件路径作为参数传递。
        String jsonString = obj.toString();//返回的PyObject对象，并将其转换为字符串jsonString。
        jsonString = URLEncoder.encode(jsonString, "ISO-8859-1");//对jsonString进行编码，以符合URL传输标准。
        String newString = URLDecoder.decode(jsonString, "UTF-8");//对编码后的字符串进行解码，以还原原始的JSON字符串。

        // 删除.txt临时文件
        //FileSystemUtils.deleteRecursively(new File(txtPath));
        return JSONObject.parseObject(newString);//返回json格式的数据
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

    // 解析json对象，将json文件中所需的字段取出来保存到Documents实体类中,便于之后将Documents实体类保存到mysql中
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

