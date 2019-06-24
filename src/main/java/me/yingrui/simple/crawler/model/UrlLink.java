package me.yingrui.simple.crawler.model;


import java.util.HashMap;
import java.util.Map;

public class UrlLink {

    private String url;
    private int depth = 0;
    private SupportHttpMethod httpMethod = SupportHttpMethod.GET;
    private Map<String, String> headers = new HashMap<>();
    private long createTime = System.currentTimeMillis();
    private String content = null;
    private String website = null;
    private String parentUrl = null;

    public UrlLink() {
    }

    public UrlLink(String url, String httpMethod, Map<String, String> headers, String content) {
        this(url, httpMethod, headers, content, null, 0);
    }


    public UrlLink(String url, String httpMethod, Map<String, String> headers, String content,
                   String parentUrl, int depth) {
        this.url = url;
        if (httpMethod.equalsIgnoreCase("POST")) {
            this.httpMethod = SupportHttpMethod.POST;
        }
        this.headers = headers;
        this.content = content;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
