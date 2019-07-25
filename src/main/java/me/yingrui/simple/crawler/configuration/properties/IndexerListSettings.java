package me.yingrui.simple.crawler.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties
public class IndexerListSettings {
    private List<IndexerSettings> indexers;
}
