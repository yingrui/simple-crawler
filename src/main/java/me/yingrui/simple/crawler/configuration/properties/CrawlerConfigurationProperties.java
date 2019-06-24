package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerConfigurationProperties {

    private List<StartUrlConfiguration> startUrls;

    public List<StartUrlConfiguration> getStartUrls() {
        return startUrls;
    }

    public void setStartUrls(List<StartUrlConfiguration> startUrls) {
        this.startUrls = startUrls;
    }
}
