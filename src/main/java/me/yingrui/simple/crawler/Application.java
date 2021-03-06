package me.yingrui.simple.crawler;

import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.configuration.properties.Wrappers;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.WebLink;
import me.yingrui.simple.crawler.service.*;
import me.yingrui.simple.crawler.service.indexer.Indexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Map;

import static me.yingrui.simple.crawler.util.JsonUtils.toJson;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "me.yingrui.simple.crawler")
public class Application implements CommandLineRunner {

    @Autowired
    private Crawler crawler;
    @Autowired
    private Wrappers wrappers;

    @Qualifier("indexers")
    @Autowired
    private Map<String, Indexer> indexers;
    @Autowired
    private WebLinkRepository webLinkRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].startsWith("--wrap-website=")) {
            String website = args[0].split("=")[1];
            webLinkRepository.scan(website, webLink -> wrap(webLink));
        } else if (args.length > 0 && args[0].equalsIgnoreCase("--crawler-run")) {
            crawler.run();
        }

        indexers.values().forEach(Indexer::close);
        System.out.println("Exit...");
        System.exit(0);
    }

    private void wrap(WebLink webLink) {
        System.out.println(webLink.getRowKey());
        WrapperSettings wrapperSettings = wrappers.getWrapper(webLink.getWebsite());
        if (wrapperSettings.isMatch(webLink.getUrl())) {
            Wrapper wrapper = new Wrapper(wrapperSettings);
            Map<String, Object> obj = wrapper.wrap(webLink);
            System.out.println(toJson(obj));
            //TODO: do we still need option: "--wrap-website=" ???
            //indexer.index(obj);
        }
    }
}
