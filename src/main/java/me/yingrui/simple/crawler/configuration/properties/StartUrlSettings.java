package me.yingrui.simple.crawler.configuration.properties;


import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
public class StartUrlSettings {

    private String startUrl;
    private String httpMethod;
    private Map<String, String> headers;
    private String bodyTemplate;
    private LinkExtractorSettings links;
    private PaginationSettings pagination;

    public CrawlerTask toCrawlerTask() {
        CrawlerTask crawlerTask = new CrawlerTask(startUrl, httpMethod, headers, bodyTemplate, links, pagination);
        return crawlerTask;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public void setLinks(LinkExtractorSettings links) {
        this.links = links;
    }

    public PaginationSettings getPagination() {
        return pagination;
    }

    public void setPagination(PaginationSettings pagination) {
        this.pagination = pagination;
    }
}
