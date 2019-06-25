package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LinkExtractorFactory {

    public LinkExtractor create(CrawlerTask crawlerTask) {
        if (crawlerTask.getResponseContentType() != null) {
            if (crawlerTask.getResponseContentType().startsWith("application/json")) {
                return new JsonLinkExtractor();
            }
        }

        return new LinkExtractor() {
            @Override
            public List<CrawlerTask> extract(CrawlerTask crawlerTask) {
                return new ArrayList<>();
            }
        };
    }

}
