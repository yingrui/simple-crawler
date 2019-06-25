package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerSettings {

    private List<StartUrlSettings> startUrls;

    public List<StartUrlSettings> getStartUrls() {
        return startUrls;
    }

    public void setStartUrls(List<StartUrlSettings> startUrls) {
        this.startUrls = startUrls;
    }
}
