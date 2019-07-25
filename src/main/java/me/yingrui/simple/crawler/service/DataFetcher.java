package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.SupportHttpMethod;
import me.yingrui.simple.crawler.model.WebLink;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;

@Service
public class DataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFetcher.class);

    private HttpClient httpClient;

    public DataFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public WebLink fetch(CrawlerTask crawlerTask) throws IOException {
        HttpRequestBase request = getRequest(crawlerTask);
        HttpResponse response = getHttpResponse(request);
        if (response != null) {
            int statusCode = response.getStatusLine().getStatusCode();
            crawlerTask.setStatusCode(statusCode);
            if (statusCode == 200) {
                if (response.getHeaders("Content-Type").length > 0) {
                    String contentType = response.getFirstHeader("Content-Type").getValue();
                    crawlerTask.setResponseContentType(contentType);
                }
                if (!isNullOrEmpty(crawlerTask.getEncode())) {
                    final String responseContent = new String(EntityUtils.toString(response.getEntity()).getBytes(crawlerTask.getEncode()));
                    crawlerTask.setResponseContent(responseContent);
                } else {
                    crawlerTask.setResponseContent(EntityUtils.toString(response.getEntity()));
                }
                return crawlerTask.toWebLink();
            }
        }
        return null;
    }

    private HttpResponse getHttpResponse(HttpRequestBase request) {
        int retry = 0;
        HttpResponse response = null;
        while (retry < 3 && response == null) {
            try {
                response = httpClient.execute(request);
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
                sleep(3000);
            }
        }

        return response;
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e1) {
            LOGGER.error(e1.getMessage());
        }
    }

    private HttpRequestBase getRequest(CrawlerTask crawlerTask) {
        HttpRequestBase request = createHttpRequest(crawlerTask);
        setHeaders(crawlerTask, request);
        return request;
    }

    private void setHeaders(CrawlerTask crawlerTask, HttpRequestBase request) {
        if (crawlerTask.getRequestHeaders() != null) {
            for (String headerName : crawlerTask.getRequestHeaders().keySet()) {
                request.setHeader(headerName, crawlerTask.getRequestHeaders().get(headerName));
            }
        }
    }

    private HttpRequestBase createHttpRequest(CrawlerTask crawlerTask) {
        if (crawlerTask.getHttpMethod() == SupportHttpMethod.POST) {
            final String contentType = crawlerTask.getRequestHeaders().get("content-type");
            if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
                return newHttpPostRequestWithForm(crawlerTask);
            } else {
                return newHttpPostRequest(crawlerTask);
            }
        } else {
            return new HttpGet(crawlerTask.getRequestUrl());
        }
    }

    private HttpRequestBase newHttpPostRequest(CrawlerTask crawlerTask) {
        HttpPost request = new HttpPost(crawlerTask.getRequestUrl());
        StringEntity entity = createRequestEntity(crawlerTask);
        request.setEntity(entity);
        return request;
    }

    private HttpRequestBase newHttpPostRequestWithForm(CrawlerTask crawlerTask) {
        HttpPost request = new HttpPost(crawlerTask.getRequestUrl());
        final List<BasicNameValuePair> nameValuePairs = Arrays.stream(crawlerTask.getRequestBody().split("&"))
                .map(it -> {
                    final String[] nameValue = it.split("=");
                    return new BasicNameValuePair(nameValue[0], nameValue[1]);
                }).collect(Collectors.toList());
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
