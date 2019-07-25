package me.yingrui.simple.crawler.service.indexer;

import java.util.Map;

public interface Indexer {
    void index(Map<String, Object> obj);

    void close();
}
