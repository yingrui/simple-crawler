package me.yingrui.simple.crawler;


import me.yingrui.simple.crawler.dao.ArticleRepository;
import me.yingrui.simple.crawler.model.UrlLink;
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

    private Queue<UrlLink> queue = new LinkedList<>();

    public void run() {
        while (!this.queue.isEmpty()) {
            UrlLink urlLink = this.queue.poll();
            LOGGER.info(urlLink.getUrl());
            fetchAndProcess(urlLink);
        }
    }

    private void fetchAndProcess(UrlLink urlLink) {
        try {
            dataFetcher.fetch(urlLink);
            System.out.println(urlLink.getResponseContent());

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public boolean add(UrlLink urlLink) {
        return this.queue.add(urlLink);
    }
}
