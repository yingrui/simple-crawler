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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class DataFetcher {

    private HttpClient httpClient;

    public DataFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void fetch(UrlLink urlLink) throws IOException {
        HttpRequestBase request = getRequest(urlLink);
        HttpResponse response = httpClient.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        urlLink.setStatusCode(statusCode);
        if (statusCode == 200) {
            String contentType = response.getFirstHeader("Content-Type").getValue();
            urlLink.setResponseContentType(contentType);
            urlLink.setResponseContent(EntityUtils.toString(response.getEntity()));
        }
    }

    private HttpRequestBase getRequest(UrlLink urlLink) {
        HttpRequestBase request = createHttpRequest(urlLink);
        setHeaders(urlLink, request);
        return request;
    }

    private void setHeaders(UrlLink urlLink, HttpRequestBase request) {
        for (String headerName : urlLink.getRequestHeaders().keySet()) {
            request.setHeader(headerName, urlLink.getRequestHeaders().get(headerName));
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
            reqEntity = new StringEntity(urlLink.getRequestBody());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return reqEntity;
    }
}
