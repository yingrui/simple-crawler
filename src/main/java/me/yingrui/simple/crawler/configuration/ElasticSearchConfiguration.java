package me.yingrui.simple.crawler.configuration;

import com.google.common.collect.ImmutableMap;
import me.yingrui.simple.crawler.configuration.properties.IndexerSettings;
import me.yingrui.simple.crawler.service.ElasticSearchIndexer;
import org.apache.http.client.HttpClient;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ElasticSearchConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

    @Bean
    public ElasticSearchIndexer elasticSearchIndexer(IndexerSettings indexerSettings, HttpClient httpClient) {
        Settings settings = Settings.builder()
                .put("cluster.name", indexerSettings.getClusterName()).build();
        TransportClient client = new PreBuiltTransportClient(settings);

        for (TransportAddress socketAddress: indexerSettings.getClusterNodes()) {
            client.addTransportAddress(socketAddress);
        }

        prepareIndex(indexerSettings, client);
        return new ElasticSearchIndexer(indexerSettings, client);
    }

    private void prepareIndex(IndexerSettings indexerSettings, TransportClient transportClient) {
        try {
            IndicesAdminClient indicesAdminClient = transportClient.admin().indices();
            CreateIndexRequestBuilder indexBuilder = indicesAdminClient.prepareCreate(indexerSettings.getIndex());
            Map<String, Object> fields = new HashMap<>();
            for (String field : indexerSettings.getStringFields()) {
                fields.put(field, ImmutableMap.of("type", "keyword"));
            }
            indexBuilder.addMapping(indexerSettings.getType(), ImmutableMap.of("properties", fields));
            indexBuilder.get();
        } catch (ResourceAlreadyExistsException e) {
            LOGGER.info("Index already exists.");
        }
    }

}
