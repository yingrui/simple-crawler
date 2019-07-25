package me.yingrui.simple.crawler.service.indexer;

import java.util.Map;

public interface Indexer {

    String getType();

    void index(Map<String, Object> obj);

    void close();
}
