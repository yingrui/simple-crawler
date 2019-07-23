package me.yingrui.simple.crawler.service.link;

import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class LinkExtractorFactory {

    @Autowired
    private WebLinkRepository webLinkRepository;

    public LinkExtractor create(CrawlerTask crawlerTask) {
        if (crawlerTask.getUrl().contains("data.stats.gov")) {
            return new StatsGovLinkExtractor(webLinkRepository);
        }
        if (isNotEmpty(crawlerTask.getResponseContentType())) {
            if (crawlerTask.getResponseContentType().startsWith("application/json")) {
                return new JsonLinkExtractor(webLinkRepository);
            }

            if (crawlerTask.getResponseContentType().startsWith("text/html")) {
                return new HtmlLinkExtractor(webLinkRepository);
            }
        }

        return stubLinkExtractor;
    }

    private LinkExtractor stubLinkExtractor = crawlerTask -> new ArrayList<>();

}
