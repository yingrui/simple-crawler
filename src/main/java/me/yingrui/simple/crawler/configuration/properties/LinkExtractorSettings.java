package me.yingrui.simple.crawler.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
public class LinkExtractorSettings {

    private boolean newLinksOnly = true;
    private String path;
    private String prefix;
    private String httpMethod = "GET";
    private String urlTemplate = "[(${url})]";
    private Map<String, String> headers;
    private String bodyTemplate;

    public LinkExtractorSettings() {

    }

    public LinkExtractorSettings(String path, String prefix, String httpMethod, String urlTemplate, Map<String, String> headers, String bodyTemplate) {
        this.path = path;
        this.prefix = prefix;
        this.httpMethod = httpMethod;
        this.urlTemplate = urlTemplate;
        this.headers = headers;
        this.bodyTemplate = bodyTemplate;
    }


    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public boolean isNewLinksOnly() {
        return newLinksOnly;
    }

    public void setNewLinksOnly(boolean newLinksOnly) {
        this.newLinksOnly = newLinksOnly;
    }
}
