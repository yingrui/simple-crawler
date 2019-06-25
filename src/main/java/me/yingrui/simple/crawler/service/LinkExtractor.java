package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.CrawlerTask;

import java.util.List;

public interface LinkExtractor {

    List<CrawlerTask> extract(CrawlerTask crawlerTask);

}
