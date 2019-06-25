package me.yingrui.simple.crawler.model;


import me.yingrui.simple.crawler.configuration.properties.LinkExtractorSettings;

import java.util.HashMap;
import java.util.Map;

public class CrawlerTask {

    private String url;
    private LinkExtractorSettings linkExtractorSettings;
    private int depth = 0;
    private SupportHttpMethod httpMethod = SupportHttpMethod.GET;
    private Map<String, String> requestHeaders = new HashMap<>();
    private long createTime = System.currentTimeMillis();
    private String requestBody = null;
    private int statusCode = 0;
    private String responseContent = null;
    private String responseContentType = null;
    private String website = null;
    private String parentUrl = null;

    public CrawlerTask() {
    }

    public CrawlerTask(String url, String httpMethod, Map<String, String> requestHeaders, String requestBody,
                       LinkExtractorSettings linkExtractorSettings) {
        this(url, httpMethod, requestHeaders, requestBody, linkExtractorSettings, null, 0);
    }


    public CrawlerTask(String url, String httpMethod, Map<String, String> requestHeaders, String requestBody,
                       LinkExtractorSettings linkExtractorSettings, String parentUrl, int depth) {
        this.url = url;
        this.linkExtractorSettings = linkExtractorSettings;
        if (httpMethod.equalsIgnoreCase("POST")) {
            this.httpMethod = SupportHttpMethod.POST;
        }
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.parentUrl = parentUrl;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SupportHttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(SupportHttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public LinkExtractorSettings getLinkExtractorSettings() {
        return linkExtractorSettings;
    }

    public void setLinkExtractorSettings(LinkExtractorSettings linkExtractorSettings) {
        this.linkExtractorSettings = linkExtractorSettings;
    }
}
