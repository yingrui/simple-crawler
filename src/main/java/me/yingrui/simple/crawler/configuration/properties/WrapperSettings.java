package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class WrapperSettings {

    private String website;
    private String matchedUrl;

    private List<ExtractorSettings> extractors;

    public List<ExtractorSettings> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<ExtractorSettings> extractors) {
        this.extractors = extractors;
    }

    public String getMatchedUrl() {
        return matchedUrl;
    }

    public void setMatchedUrl(String matchedUrl) {
        this.matchedUrl = matchedUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isMatch(String url) {
        return url.matches(matchedUrl);
    }
}
