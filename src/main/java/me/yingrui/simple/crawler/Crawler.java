package me.yingrui.simple.crawler;


import me.yingrui.simple.crawler.dao.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Crawler {

    @Autowired
    ArticleRepository articleRepository;

    public void run(String startUrl) {
        System.out.println(startUrl);
    }

}
