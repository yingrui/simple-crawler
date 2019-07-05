package me.yingrui.simple.crawler.configuration.properties;


import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
public class ExtractorSettings {

    private String field;
    private String path;
    private String type = "String";
    private String extractor;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtractor() {
        return extractor;
    }

    public void setExtractor(String extractor) {
        this.extractor = extractor;
    }
}
