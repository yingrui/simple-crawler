package me.yingrui.simple.crawler;

import me.yingrui.simple.crawler.configuration.properties.CrawlerConfigurationProperties;
import me.yingrui.simple.crawler.configuration.properties.StartUrlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "me.yingrui.simple.crawler")
public class Application implements CommandLineRunner {

    @Autowired
    Crawler crawler;
    @Autowired
    CrawlerConfigurationProperties crawlerConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        for (StartUrlConfiguration startUrl : crawlerConfiguration.getStartUrls()) {
            crawler.add(startUrl.toUrlLink());
        }
        crawler.run();
    }
}
