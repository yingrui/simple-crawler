package me.yingrui.simple.crawler.service.extractor;

public interface Extractor {

    public void setSource(Object source);

    public Object extract();

}
