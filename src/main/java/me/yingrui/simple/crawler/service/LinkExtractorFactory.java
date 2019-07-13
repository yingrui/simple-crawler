package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class LinkExtractorFactory {

    @Autowired
    private WebLinkRepository webLinkRepository;

    public LinkExtractor create(CrawlerTask crawlerTask) {
        if (isNotEmpty(crawlerTask.getResponseContentType())) {
            if (crawlerTask.getResponseContentType().startsWith("application/json")) {
                return new JsonLinkExtractor(webLinkRepository);
            }
        }

        return stubLinkExtractor;
    }

    private LinkExtractor stubLinkExtractor = crawlerTask -> new ArrayList<>();

}
