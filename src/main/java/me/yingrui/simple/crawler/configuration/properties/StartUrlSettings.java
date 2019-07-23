package me.yingrui.simple.crawler.configuration.properties;


import lombok.Data;
import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Data
public class StartUrlSettings {

    private String url;
    private String startUrl;
    private String httpMethod;
    private Map<String, String> headers;
    private String dataTemplate;
    private String encode;
    private LinkExtractorSettings links;
    private PaginationSettings pagination;

    public CrawlerTask toCrawlerTask() {
        return new CrawlerTask(url, startUrl, httpMethod, headers, dataTemplate, encode, links, pagination);
    }
}
