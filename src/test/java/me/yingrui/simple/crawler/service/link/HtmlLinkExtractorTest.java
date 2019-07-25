package me.yingrui.simple.crawler.service.link;

import me.yingrui.simple.crawler.configuration.properties.LinkExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.PaginationSettings;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.SupportHttpMethod;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HtmlLinkExtractorTest {

    String linkPath = ".entry-title a";

    @Test
    public void should_extract_link_from_json() {
        LinkExtractorSettings linkExtractorSettings = new LinkExtractorSettings(linkPath, null, "GET",
                null, new HashMap<>(), null);

        CrawlerTask crawlerTask = new CrawlerTask("url", "https://insights.thoughtworks.cn/", "kafka", "GET", null, null, null, linkExtractorSettings, null);
        crawlerTask.setResponseContent(html());
        crawlerTask.setResponseContentType("text/html; charset=UTF-8");

        LinkExtractor linkExtractor = new HtmlLinkExtractor();
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        assertEquals(15, links.size());
        CrawlerTask child = links.get(0);
        assertEquals("https://insights.thoughtworks.cn/container-technology-and-micro-services/", child.getUrl());
        assertEquals(child.getUrl(), child.getRequestUrl());
        assertEquals("", child.getRequestBody());
        assertEquals(0, child.getRequestHeaders().size());
    }

    @Test
    public void should_extract_next_page_crawler_task() {
        String startUrl = "https://insights.thoughtworks.cn/";
        String nextPagePath = "a.next";

        LinkExtractorSettings linkExtractorSettings = new LinkExtractorSettings(linkPath, null, "GET",
                null, new HashMap<>(), null);

        PaginationSettings paginationSettings = new PaginationSettings(nextPagePath, null, null, null);
        paginationSettings.setNoPagination(false);

        CrawlerTask crawlerTask = new CrawlerTask("url", startUrl,"kafka", "GET", defaultHeaders(), "",
                null, linkExtractorSettings, paginationSettings);
        crawlerTask.setResponseContent(html());
        crawlerTask.setResponseContentType("text/html; charset=UTF-8");

        LinkExtractor linkExtractor = new HtmlLinkExtractor();
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        assertEquals(16, links.size());

        CrawlerTask nextPageCrawlerTask = links.get(links.size() - 1);
        assertEquals("https://insights.thoughtworks.cn/page/2/", nextPageCrawlerTask.getUrl());
        assertEquals(nextPageCrawlerTask.getUrl(), nextPageCrawlerTask.getRequestUrl());
        assertEquals("", nextPageCrawlerTask.getRequestBody());
        assertEquals(0, nextPageCrawlerTask.getDepth());
        assertEquals(SupportHttpMethod.GET, nextPageCrawlerTask.getHttpMethod());
    }


    private String html() {
        try {
            return IOUtils.toString(ResourceUtils.getURL("classpath:page.html"));
        } catch (IOException e) {
            return null;
        }
    }

    private Map<String, String> defaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }
}