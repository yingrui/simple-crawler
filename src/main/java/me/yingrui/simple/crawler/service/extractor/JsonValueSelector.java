package me.yingrui.simple.crawler.service.extractor;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JsonValueSelector implements ValueSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonValueSelector.class);
    private DocumentContext jsonContext;

    @Override
    public void setContent(String content) {
        jsonContext = JsonPath.parse(content);
    }

    @Override
    public List<String> getListValues(String path) {
        try {
            List<String> list = jsonContext.read(path);
            return list;
        } catch (PathNotFoundException e) {
            LOGGER.error("get list values: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Object getValue(String path) {
        try {
            return jsonContext.read(path);
        } catch (PathNotFoundException e) {
            LOGGER.error("get list values: " + e.getMessage());
        }
        return null;
    }
}
