package me.yingrui.simple.crawler.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerSettings {

    @Getter
    @Setter
    private List<StartUrlSettings> startUrls;
}
