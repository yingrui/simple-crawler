package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "wrapper")
public class WrapperSettings {

    private List<ExtractorSettings> extractors;

    public List<ExtractorSettings> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<ExtractorSettings> extractors) {
        this.extractors = extractors;
    }
}
