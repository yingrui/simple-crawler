package me.yingrui.simple.crawler;

import me.yingrui.simple.crawler.dao.WebLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "me.yingrui.simple.crawler")
public class Application implements CommandLineRunner {

    @Autowired
    private Crawler crawler;
    @Autowired
    private WebLinkRepository webLinkRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].startsWith("--wrap-website=")) {
            String website = args[0].split("=")[1];
            webLinkRepository.scan(website, webLink -> {
                System.out.println(webLink.getRowKey());
            });
        } else if (args.length > 0 && args[0].equalsIgnoreCase("--crawler-run")) {
            crawler.run();
        }
    }
}
