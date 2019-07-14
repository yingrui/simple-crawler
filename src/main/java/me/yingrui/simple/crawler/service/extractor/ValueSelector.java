package me.yingrui.simple.crawler.service.extractor;

import java.util.List;

public interface ValueSelector {
    void setContent(String content);

    List<String> getListValues(String path);

    Object getValue(String path);
}
