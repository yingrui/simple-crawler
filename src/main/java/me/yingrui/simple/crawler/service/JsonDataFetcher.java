package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.SupportHttpMethod;
import me.yingrui.simple.crawler.model.UrlLink;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JsonDataFetcher implements DataFetcher {

    private HttpClient httpClient;

    public JsonDataFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String fetch(UrlLink urlLink) throws IOException {
        HttpRequestBase request = getRequest(urlLink);
        HttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    private HttpRequestBase getRequest(UrlLink urlLink) {
        HttpRequestBase request = createHttpRequest(urlLink);
        setHeaders(urlLink, request);
        return request;
    }

    private void setHeaders(UrlLink urlLink, HttpRequestBase request) {
        for (String headerName : urlLink.getHeaders().keySet()) {
            request.setHeader(headerName, urlLink.getHeaders().get(headerName));
        }
    }

    private HttpRequestBase createHttpRequest(UrlLink urlLink) {
        if (urlLink.getHttpMethod() == SupportHttpMethod.POST) {
            return newHttpPostRequest(urlLink);
        } else {
            return new HttpGet(urlLink.getUrl());
        }
    }

    private HttpRequestBase newHttpPostRequest(UrlLink urlLink) {
        HttpPost request = new HttpPost(urlLink.getUrl());
        StringEntity entity = createRequestEntity(urlLink);
        request.setEntity(entity);
        return request;
    }

    private StringEntity createRequestEntity(UrlLink urlLink) {
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(urlLink.getContent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return reqEntity;
    }
}
