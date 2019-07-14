package me.yingrui.simple.crawler.fixture;

import me.yingrui.simple.crawler.configuration.properties.ExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;

import java.util.ArrayList;

public class WrapperTestBase {

    public WrapperSettings getWrapperSettings(ExtractorSettings extractor) {
        WrapperSettings settings = new WrapperSettings();
        ArrayList<ExtractorSettings> extractors = new ArrayList<>();
        extractors.add(extractor);
        settings.setExtractors(extractors);
        return settings;
    }

    public ExtractorSettings extractorSettings(String path, String field) {
        ExtractorSettings settings = new ExtractorSettings();
        settings.setField(field);
        settings.setPath(path);
        return settings;
    }

    public ExtractorSettings extractorSettings(String path, String field, String type) {
        ExtractorSettings settings = new ExtractorSettings();
        settings.setField(field);
        settings.setPath(path);
        settings.setType(type);
        return settings;
    }

    public ExtractorSettings extractorSettings(String path, String field, String type, String extractorClass) {
        ExtractorSettings settings = new ExtractorSettings();
        settings.setField(field);
        settings.setPath(path);
        settings.setType(type);
        settings.setExtractor(extractorClass);
        return settings;
    }

}