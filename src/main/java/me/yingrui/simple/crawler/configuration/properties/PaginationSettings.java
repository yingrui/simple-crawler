package me.yingrui.simple.crawler.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class PaginationSettings {

    private boolean stopWhenAllLinksCrawled;
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
}
