package me.yingrui.simple.crawler.service.extractor;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HtmlValueSelector implements ValueSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlValueSelector.class);
    private Document doc;

    @Override
    public void setContent(String content) {
        doc = Jsoup.parse(content);
    }

    @Override
    public List<String> getListValues(String path) {
        Elements elements = doc.select(path);
        List<String> list = Lists.newArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            list.add(element.text());
        }

        return list.size() > 0 ? list : null;
    }

    @Override
    public Object getValue(String path) {
        try {
            return doc.selectFirst(path).text();
        } catch (Exception e) {
            LOGGER.error("get value: " + e.getMessage());
        }
        return null;
    }
}
