package me.yingrui.simple.crawler.service;

import java.util.Map;

public interface Indexer {
    void index(Map<String, Object> obj);

    void close();
}
