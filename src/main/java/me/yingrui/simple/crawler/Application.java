package me.yingrui.simple.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "me.yingrui.simple.crawler")
public class Application implements CommandLineRunner {

    @Autowired
    Crawler crawler;
    @Value("${crawler.start-url:}")
    String startUrl;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        crawler.run(startUrl);
    }
}
