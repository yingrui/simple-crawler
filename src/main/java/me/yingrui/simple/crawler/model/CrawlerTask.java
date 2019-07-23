package me.yingrui.simple.crawler.model;


import lombok.Data;
import lombok.ToString;
import me.yingrui.simple.crawler.configuration.properties.LinkExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.PaginationSettings;

import java.util.HashMap;
import java.util.Map;

@ToString
@Data
public class CrawlerTask {

    private String url;
    private String requestUrl;
    private LinkExtractorSettings linkExtractorSettings;
    private PaginationSettings paginationSettings;
    private int depth = 0;
    private SupportHttpMethod httpMethod = SupportHttpMethod.GET;
    private Map<String, String> requestHeaders = new HashMap<>();
    private long createTime = System.currentTimeMillis();
    private String requestBody = null;
    private int statusCode = 0;
    private String encode;
    private String responseContent = null;
    private String responseContentType = null;
    private String parentUrl = null;

    public CrawlerTask(String url, String requestUrl, String httpMethod, Map<String, String> requestHeaders, String requestBody,
                       String encode, LinkExtractorSettings linkExtractorSettings, PaginationSettings paginationSettings) {
        this(url, requestUrl, httpMethod, requestHeaders, requestBody, encode, linkExtractorSettings, paginationSettings, null, 0);
    }

    public CrawlerTask(String url, String requestUrl, String httpMethod, Map<String, String> requestHeaders, String requestBody,
                       String encode, LinkExtractorSettings linkExtractorSettings, PaginationSettings paginationSettings,
                       String parentUrl, int depth) {
        this.url = url;
        this.requestUrl = requestUrl;
        if (requestUrl != null && url == null) {
            this.url = requestUrl;
        }

        this.linkExtractorSettings = linkExtractorSettings;
        this.paginationSettings = paginationSettings;
        if (httpMethod == null) {
            this.httpMethod = SupportHttpMethod.GET;
        } else if (httpMethod.equalsIgnoreCase("POST")) {
            this.httpMethod = SupportHttpMethod.POST;
        }
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.parentUrl = parentUrl;
        this.depth = depth;
        this.encode = encode;
    }

    public WebLink toWebLink() {
        return new WebLink(url, createTime, responseContent, responseContentType, parentUrl, depth);
    }
}
