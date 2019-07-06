package me.yingrui.simple.crawler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static String toJson(Map<String, Object> obj) {
        try {
            return objectMapper.writer().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
