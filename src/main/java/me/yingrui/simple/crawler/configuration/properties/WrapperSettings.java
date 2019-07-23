package me.yingrui.simple.crawler.configuration.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
@Data
public class WrapperSettings {

    private String website;
    private String matchUrl;
    private String notMatchUrl;
    private List<ExtractorSettings> extractors;

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
