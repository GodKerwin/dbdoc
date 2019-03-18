package com.xul.dbdoc.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lxu on 2018/12/14.
 */
@Slf4j
public class FreemarkerUtils {

    public static final String UTF8 = "utf-8";

    public static byte[] getBytes(Object dataModel) throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(UTF8);
        InputStream stream = FreemarkerUtils.class.getClassLoader().getResourceAsStream("数据库文档模板.ftl");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = stream.read()) != -1) {
            outputStream.write(ch);
        }
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("数据库文档模板.ftl", outputStream.toString(UTF8));
        configuration.setTemplateLoader(stringTemplateLoader);
        Template t = configuration.getTemplate("数据库文档模板.ftl", UTF8);
        outputStream = new ByteArrayOutputStream();
        t.process(dataModel, new OutputStreamWriter(outputStream, UTF8));
        return outputStream.toByteArray();
    }

    public static HttpHeaders headers(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String encodeFileName = fileName;
        try {
            encodeFileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        headers.setContentDispositionFormData("attachment", encodeFileName);
        return headers;
    }
}
