package me.yingrui.simple.crawler.service;


import me.yingrui.simple.crawler.configuration.properties.CrawlerSettings;
import me.yingrui.simple.crawler.configuration.properties.StartUrlSettings;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.WebLink;
import me.yingrui.simple.crawler.service.link.LinkExtractor;
import me.yingrui.simple.crawler.service.link.LinkExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private Queue<CrawlerTask> queue = new LinkedList<>();
    private Random random = new Random();

    private DataFetcher dataFetcher;
    private LinkExtractorFactory linkExtractorFactory;
    private CrawlerSettings crawlerSettings;
    private WebLinkRepository webLinkRepository;

    public Crawler(DataFetcher dataFetcher, LinkExtractorFactory linkExtractorFactory, CrawlerSettings crawlerSettings, WebLinkRepository webLinkRepository) {
        this.dataFetcher = dataFetcher;
        this.linkExtractorFactory = linkExtractorFactory;
        this.crawlerSettings = crawlerSettings;
        this.webLinkRepository = webLinkRepository;
        initializeQueue();
    }

    private void initializeQueue() {
        for (StartUrlSettings startUrl : this.crawlerSettings.getStartUrls()) {
            add(startUrl.toCrawlerTask());
        }
    }

    public void run() {
        while (!this.queue.isEmpty()) {
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
                webLinkRepository.save(webLink);

                extractLinks(crawlerTask);

                Thread.sleep(random.nextInt(2000));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void extractLinks(CrawlerTask crawlerTask) {
        LinkExtractor linkExtractor = linkExtractorFactory.create(crawlerTask);
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        for (CrawlerTask child : links) {
            this.add(child);
        }
    }

    public boolean add(CrawlerTask crawlerTask) {
        return this.queue.add(crawlerTask);
    }
}
