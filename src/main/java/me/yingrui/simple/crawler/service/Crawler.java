package me.yingrui.simple.crawler.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.yingrui.simple.crawler.configuration.properties.CrawlerSettings;
import me.yingrui.simple.crawler.configuration.properties.StartUrlSettings;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.configuration.properties.Wrappers;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.WebLink;
import me.yingrui.simple.crawler.service.indexer.Indexer;
import me.yingrui.simple.crawler.service.link.LinkExtractor;
import me.yingrui.simple.crawler.service.link.LinkExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private Queue<CrawlerTask> queue = new LinkedList<>();
    private Random random = new Random();

    private DataFetcher dataFetcher;
    private LinkExtractorFactory linkExtractorFactory;
    private CrawlerSettings crawlerSettings;
    private WebLinkRepository webLinkRepository;
    private Wrappers wrappers;
    private Map<String, Indexer> indexers;

    @Autowired
    public Crawler(DataFetcher dataFetcher, LinkExtractorFactory linkExtractorFactory, CrawlerSettings crawlerSettings, WebLinkRepository webLinkRepository, Wrappers wrappers, @Qualifier("indexers") Map<String, Indexer> indexers) {
        this.dataFetcher = dataFetcher;
        this.linkExtractorFactory = linkExtractorFactory;
        this.crawlerSettings = crawlerSettings;
        this.webLinkRepository = webLinkRepository;
        this.wrappers = wrappers;
        this.indexers = indexers;
        initializeQueue();
    }

    private void initializeQueue() {
        for (StartUrlSettings startUrl : this.crawlerSettings.getStartUrls()) {
            add(startUrl.toCrawlerTask());
        }
    }

    public void run() {
        while (!this.queue.isEmpty()) {
            LOGGER.info("Current queue size: " + this.queue.size());
            CrawlerTask crawlerTask = this.queue.poll();
            LOGGER.info(crawlerTask.getUrl());
            fetchAndProcess(crawlerTask);
        }
        LOGGER.info("URL queue is empty, exit...");
    }

    private void fetchAndProcess(CrawlerTask crawlerTask) {
        try {
            WebLink webLink = dataFetcher.fetch(crawlerTask);
            if (webLink != null) {
                LOGGER.debug(webLink.getContent());
                webLink.setRowKey(getRowKey(crawlerTask, webLink));
                webLinkRepository.save(webLink);

                extractLinks(crawlerTask);
                wrap(webLink, crawlerTask.getIndexerType());

                Thread.sleep(random.nextInt(3000));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getRowKey(CrawlerTask crawlerTask, WebLink webLink) {
        if (isNullOrEmpty(crawlerTask.getRequestBody())) {
            return webLink.getRowKey();
        } else {
            return webLink.getRowKey() + "+" + crawlerTask.getRequestBody();
        }
    }

    private void wrap(WebLink webLink, String indexerType) {
        LOGGER.info(webLink.getRowKey());
        WrapperSettings wrapperSettings = wrappers.getWrapper(webLink.getWebsite());
        if (wrapperSettings == null) {
            return;
        }
        if (wrapperSettings.isMatch(webLink.getUrl())) {
            Wrapper wrapper = new Wrapper(wrapperSettings);
            if (wrapperSettings.getExtractors() == null || wrapperSettings.getExtractors().isEmpty()) {
                try {
                    Map<String, Object> result = new ObjectMapper().readValue(webLink.getContent(), HashMap.class);
                    indexers.get(indexerType).index(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Map<String, Object> obj = wrapper.wrap(webLink);
                indexers.get(indexerType).index(obj);
            }
        }
    }

    private void extractLinks(CrawlerTask crawlerTask) {
        LinkExtractor linkExtractor = linkExtractorFactory.create(crawlerTask);
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        this.addAll(links);
    }

    public boolean add(CrawlerTask crawlerTask) {
        return this.queue.add(crawlerTask);
    }

    public boolean addAll(List<CrawlerTask> crawlerTasks) {
        return this.queue.addAll(crawlerTasks);
    }
}
