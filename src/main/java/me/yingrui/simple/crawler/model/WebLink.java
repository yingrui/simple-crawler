package me.yingrui.simple.crawler.model;

import java.net.MalformedURLException;
import java.net.URL;

public class WebLink {

    private String rowKey;
    private String url;
    private long createTime;
    private String content;
    private String contentType;
    private String parentUrl;
    private String website;
    private int depth;

    public WebLink(String url, long createTime, String content, String contentType, String parentUrl, int depth) {
        this.url = url;
        this.createTime = createTime;
        this.content = content;
        this.contentType = contentType;
        this.parentUrl = parentUrl;
        this.depth = depth;
        calculateRowKey();
    }

    private void calculateRowKey() {
        try {
            URL u = new URL(url);
            if (this.website == null) {
                this.website = u.getHost();
            }
            this.rowKey = this.website + "+" + this.url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public void setWebsite(String website) {
        this.website = website;
        calculateRowKey();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getWebsite() {
        return website;
    }
}
