package me.yingrui.simple.crawler;


import me.yingrui.simple.crawler.dao.ArticleRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.service.DataFetcher;
import me.yingrui.simple.crawler.service.LinkExtractor;
import me.yingrui.simple.crawler.service.LinkExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DataFetcher dataFetcher;

    @Autowired
    private LinkExtractorFactory linkExtractorFactory;

    private Queue<CrawlerTask> queue = new LinkedList<>();

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
            dataFetcher.fetch(crawlerTask);
            LOGGER.debug(crawlerTask.getResponseContent());
            LinkExtractor linkExtractor = linkExtractorFactory.create(crawlerTask);
            List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
            for (CrawlerTask child : links) {
                this.add(child);
            }

            Thread.sleep(1000);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean add(CrawlerTask crawlerTask) {
        return this.queue.add(crawlerTask);
    }
}
