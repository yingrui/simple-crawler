package me.yingrui.simple.crawler.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "wrappers")
public class Wrappers {

    private List<WrapperSettings> websites;

    public WrapperSettings getWrapper(String website) {
        List<WrapperSettings> collect = websites.stream()
                .filter(wrapper -> wrapper.getWebsite().equals(website))
                .collect(Collectors.toList());
        return collect.size() > 0 ? collect.get(0) : null;
    }

    public List<WrapperSettings> getWebsites() {
        return websites;
    }

    public void setWebsites(List<WrapperSettings> websites) {
        this.websites = websites;
    }
}
