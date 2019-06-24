package me.yingrui.simple.crawler.configuration.properties;


import me.yingrui.simple.crawler.model.UrlLink;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
public class StartUrlConfiguration {

    private String startUrl;
    private String httpMethod;
    private Map<String, String> headers;
    private String body;

    public UrlLink toUrlLink() {
        UrlLink urlLink = new UrlLink(startUrl, httpMethod, headers, body);
        return urlLink;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
