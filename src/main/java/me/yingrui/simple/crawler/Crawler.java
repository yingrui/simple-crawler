package me.yingrui.simple.crawler;


import me.yingrui.simple.crawler.dao.ArticleRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.service.DataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DataFetcher dataFetcher;

    private Queue<CrawlerTask> queue = new LinkedList<>();

    public void run() {
        while (!this.queue.isEmpty()) {
            CrawlerTask crawlerTask = this.queue.poll();
            LOGGER.info(crawlerTask.getUrl());
            fetchAndProcess(crawlerTask);
        }
    }

    private void fetchAndProcess(CrawlerTask crawlerTask) {
        try {
            dataFetcher.fetch(crawlerTask);
            System.out.println(crawlerTask.getResponseContent());

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public boolean add(CrawlerTask crawlerTask) {
        return this.queue.add(crawlerTask);
    }
}
