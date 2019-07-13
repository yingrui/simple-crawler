package me.yingrui.simple.crawler.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class PaginationSettings {

    // If no pagination is true, then never try to extract links from next page. Default is true, no pagination.
    private boolean noPagination = true;
    // If extracted links on current page are crawled before, then stop trying to extract links from next page, default is true.
    private boolean stopWhenAllLinksCrawled = true;
    private String path;
    private String prefix;
    private String urlTemplate = "${url}";
    private String bodyTemplate;

    public PaginationSettings() {

    }

    public PaginationSettings(String path, String prefix, String urlTemplate, String bodyTemplate) {
        this.path = path;
        this.prefix = prefix;
        this.urlTemplate = urlTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public boolean isStopWhenAllLinksCrawled() {
        return stopWhenAllLinksCrawled;
    }

    public void setStopWhenAllLinksCrawled(boolean stopWhenAllLinksCrawled) {
        this.stopWhenAllLinksCrawled = stopWhenAllLinksCrawled;
    }

    public boolean isNoPagination() {
        return noPagination;
    }

    public void setNoPagination(boolean noPagination) {
        this.noPagination = noPagination;
    }
}
