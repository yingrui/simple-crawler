package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.SupportHttpMethod;
import me.yingrui.simple.crawler.model.CrawlerTask;
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

    public void fetch(CrawlerTask crawlerTask) throws IOException {
        HttpRequestBase request = getRequest(crawlerTask);
        HttpResponse response = httpClient.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        crawlerTask.setStatusCode(statusCode);
        if (statusCode == 200) {
            String contentType = response.getFirstHeader("Content-Type").getValue();
            crawlerTask.setResponseContentType(contentType);
            crawlerTask.setResponseContent(EntityUtils.toString(response.getEntity()));
        }
    }

    private HttpRequestBase getRequest(CrawlerTask crawlerTask) {
        HttpRequestBase request = createHttpRequest(crawlerTask);
        setHeaders(crawlerTask, request);
        return request;
    }

    private void setHeaders(CrawlerTask crawlerTask, HttpRequestBase request) {
        for (String headerName : crawlerTask.getRequestHeaders().keySet()) {
            request.setHeader(headerName, crawlerTask.getRequestHeaders().get(headerName));
        }
    }

    private HttpRequestBase createHttpRequest(CrawlerTask crawlerTask) {
        if (crawlerTask.getHttpMethod() == SupportHttpMethod.POST) {
            return newHttpPostRequest(crawlerTask);
        } else {
            return new HttpGet(crawlerTask.getUrl());
        }
    }

    private HttpRequestBase newHttpPostRequest(CrawlerTask crawlerTask) {
        HttpPost request = new HttpPost(crawlerTask.getUrl());
        StringEntity entity = createRequestEntity(crawlerTask);
        request.setEntity(entity);
        return request;
    }

    private StringEntity createRequestEntity(CrawlerTask crawlerTask) {
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(crawlerTask.getRequestBody());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return reqEntity;
    }
}
