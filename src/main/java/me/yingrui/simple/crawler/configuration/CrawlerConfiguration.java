package me.yingrui.simple.crawler.configuration;

import me.yingrui.simple.crawler.configuration.properties.CrawlerSettings;
import me.yingrui.simple.crawler.configuration.properties.Wrappers;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.service.Crawler;
import me.yingrui.simple.crawler.service.DataFetcher;
import me.yingrui.simple.crawler.service.ElasticSearchIndexer;
import me.yingrui.simple.crawler.service.link.LinkExtractorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfiguration {

    @Bean
    public Crawler crawler(DataFetcher dataFetcher, LinkExtractorFactory linkExtractorFactory,
                           CrawlerSettings crawlerSettings, WebLinkRepository webLinkRepository,
                           Wrappers wrappers, ElasticSearchIndexer elasticSearchIndexer) {
        return new Crawler(dataFetcher, linkExtractorFactory, crawlerSettings, webLinkRepository, wrappers, elasticSearchIndexer);
    }


}
