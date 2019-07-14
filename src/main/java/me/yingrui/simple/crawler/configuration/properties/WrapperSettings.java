package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class WrapperSettings {

    private String website;
    private String matchUrl;
    private String notMatchUrl;

    private List<ExtractorSettings> extractors;

    public List<ExtractorSettings> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<ExtractorSettings> extractors) {
        this.extractors = extractors;
    }

    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getNotMatchUrl() {
        return notMatchUrl;
    }

    public void setNotMatchUrl(String notMatchUrl) {
        this.notMatchUrl = notMatchUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isMatch(String url) {
        if (matchUrl != null) {
            boolean match = url.matches(matchUrl);
            if (match && notMatchUrl != null) {
                return !url.matches(notMatchUrl);
            } else {
                return match;
            }
        } else {
            return true;
        }
    }
}
