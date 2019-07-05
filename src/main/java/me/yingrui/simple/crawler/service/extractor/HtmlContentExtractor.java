package me.yingrui.simple.crawler.service.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlContentExtractor implements Extractor {

    private String html;

    public HtmlContentExtractor() {

    }

    @Override
    public void setSource(Object source) {
        html = (String) source;
    }

    @Override
    public Object extract() {
        if (html == null) {
            return null;
        }

        if (html != null) {
            Document doc = Jsoup.parse(html);
            return doc.body().text();
        }
        return null;
    }

}
