package me.yingrui.simple.crawler.service;

import com.jayway.jsonpath.PathNotFoundException;
import me.yingrui.simple.crawler.configuration.properties.ExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.model.WebLink;
import me.yingrui.simple.crawler.service.extractor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Wrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wrapper.class);

    private WrapperSettings wrapperSettings;
    private ValueSelector valueSelector;

    public Wrapper(WrapperSettings wrapperSettings) {
        this.wrapperSettings = wrapperSettings;
    }

    public Map<String, Object> wrap(WebLink webLink) {
        Map<String, Object> map = initialize(webLink);
        if (wrapperSettings!= null && wrapperSettings.isMatch(webLink.getUrl())) {
            extract(webLink, map);
        }
        return map;
    }

    private void extract(WebLink webLink, Map<String, Object> map) {
        if (wrapperSettings == null) {
            return;
        }

        initValueSelector(webLink);

        List<ExtractorSettings> extractors = wrapperSettings.getExtractors();
        for (ExtractorSettings extractorSettings : extractors) {
            extract(extractorSettings, map);
        }
    }

    private void initValueSelector(WebLink webLink) {
        String content = webLink.getContent();
        String contentType = webLink.getContentType();
        if (contentType.startsWith("application/json")) {
            valueSelector = new JsonValueSelector();
            valueSelector.setContent(content);
        } else if (contentType.startsWith("text/html")) {
            valueSelector = new HtmlValueSelector();
            valueSelector.setContent(content);
        }
    }

    private void extract(ExtractorSettings extractorSettings, Map<String, Object> map) {
        if (isFieldTypeString(extractorSettings) || isFieldTypeDatetime(extractorSettings)) {
            extractStringValue(extractorSettings, map);
        } else if (isFieldTypeArray(extractorSettings)) {
            extractListValues(extractorSettings, map);
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

    private void extractListValues(ExtractorSettings extractorSettings, Map<String, Object> map) {
        try {
            List<String> list = valueSelector.getListValues(extractorSettings.getPath());
            afterValueSelected(extractorSettings, map, list);
        } catch (PathNotFoundException e) {
            LOGGER.error("extractListValues: " + e.getMessage());
        }
    }

    private void extractStringValue(ExtractorSettings extractorSettings, Map<String, Object> map) {
        Object value = valueSelector.getValue(extractorSettings.getPath());
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
}
