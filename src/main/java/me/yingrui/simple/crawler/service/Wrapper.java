package me.yingrui.simple.crawler.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.yingrui.simple.crawler.configuration.properties.ExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.model.WebLink;
import me.yingrui.simple.crawler.service.extractor.Extractor;
import me.yingrui.simple.crawler.service.extractor.ExtractorFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class Wrapper {

    private WrapperSettings wrapperSettings;

    public Map<String, Object> wrap(WebLink webLink) {
        Map<String, Object> map = initialize(webLink);
        extract(webLink, map);

        return map;
    }

    private void extract(WebLink webLink, Map<String, Object> map) {
        if (wrapperSettings == null) {
            return;
        }

        List<ExtractorSettings> extractors = wrapperSettings.getExtractors();

        String content = webLink.getContent();
        String contentType = webLink.getContentType();
        if (contentType.startsWith("application/json")) {
            DocumentContext jsonContext = JsonPath.parse(content);
            for (ExtractorSettings extractorSettings : extractors) {
                extract(extractorSettings, jsonContext, map);
            }
        }
    }

    private void extract(ExtractorSettings extractorSettings, DocumentContext jsonContext, Map<String, Object> map) {
        if (isFieldTypeString(extractorSettings) || isFieldTypeDatetime(extractorSettings)) {
            extractStringValue(extractorSettings, jsonContext, map);
        } else if (isFieldTypeArray(extractorSettings)) {
            extractListValues(extractorSettings, jsonContext, map);
        }
    }

    private boolean isFieldTypeString(ExtractorSettings extractorSettings) {
        return extractorSettings.getType().equalsIgnoreCase("String");
    }

    private boolean isFieldTypeDatetime(ExtractorSettings extractorSettings) {
        return extractorSettings.getType().equalsIgnoreCase("Datetime");
    }

    private boolean isFieldTypeArray(ExtractorSettings extractorSettings) {
        return extractorSettings.getType().equalsIgnoreCase("Array");
    }

    private void extractListValues(ExtractorSettings extractorSettings, DocumentContext jsonContext, Map<String, Object> map) {
        List<String> list = jsonContext.read(extractorSettings.getPath());
        afterValueSelected(extractorSettings, map, list);
    }

    private void extractStringValue(ExtractorSettings extractorSettings, DocumentContext jsonContext, Map<String, Object> map) {
        Object value = jsonContext.read(extractorSettings.getPath());
        afterValueSelected(extractorSettings, map, value);
    }

    private void afterValueSelected(ExtractorSettings extractorSettings, Map<String, Object> map, Object obj) {
        String extractorClass = extractorSettings.getExtractor();

        if (extractorClass != null) {
            Extractor extractor = ExtractorFactory.getInstance().create(extractorClass, obj);
            Object data = extractor.extract();
            map.put(extractorSettings.getField(), data);
        } else {
            map.put(extractorSettings.getField(), obj);
        }
    }

    private Map<String, Object> initialize(WebLink webLink) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("url", webLink.getUrl());
        map.put("website", webLink.getWebsite());
        map.put("depth", webLink.getDepth());
        return map;
    }

    public void setWrapperSettings(WrapperSettings wrapperSettings) {
        this.wrapperSettings = wrapperSettings;
    }
}
