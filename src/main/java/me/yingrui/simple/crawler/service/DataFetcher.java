package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.model.UrlLink;

import java.io.IOException;

public interface DataFetcher {

    String fetch(UrlLink urlLink) throws IOException;

}
