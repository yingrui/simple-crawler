package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.SupportHttpMethod;
import me.yingrui.simple.crawler.model.UrlLink;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataFetchFactory {

    @Autowired
    private HttpClient httpClient;

    public DataFetcher create(UrlLink urlLink) {
        if (urlLink.getHttpMethod() == SupportHttpMethod.POST) {
            return new JsonDataFetcher(httpClient);
        }
        return null;
    }

}
