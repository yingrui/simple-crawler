package me.yingrui.simple.crawler.configuration;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import me.yingrui.simple.crawler.configuration.properties.IndexerListSettings;
import me.yingrui.simple.crawler.configuration.properties.IndexerSettings;
import me.yingrui.simple.crawler.service.indexer.ElasticSearchIndexer;
import me.yingrui.simple.crawler.service.indexer.Indexer;
import me.yingrui.simple.crawler.service.indexer.KafkaIndexer;
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

import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
public class IndexerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerConfiguration.class);

    @Bean(name = "indexers")
    public Map<String, Indexer> indexer(IndexerListSettings indexerListSettings) {
        return indexerListSettings.getIndexers().stream().map(indexerSettings -> {
            if (indexerSettings.getIndexerType().equalsIgnoreCase("kafka")) {
                return new KafkaIndexer(indexerSettings.getPlainNodes(), indexerSettings.getIndex());
            } else {
                return elasticSearchIndexer(indexerSettings);
            }
        }).collect(toMap(Indexer::getType, it -> it));
    }

    private ElasticSearchIndexer elasticSearchIndexer(IndexerSettings indexerSettings) {
        Settings settings = Settings.builder()
                .put("cluster.name", indexerSettings.getClusterName()).build();
        TransportClient client = new PreBuiltTransportClient(settings);

        for (TransportAddress socketAddress : indexerSettings.getClusterNodes()) {
            client.addTransportAddress(socketAddress);
        }

        prepareESIndex(indexerSettings, client);
        return new ElasticSearchIndexer(indexerSettings, client);
    }

    private void prepareESIndex(IndexerSettings indexerSettings, TransportClient transportClient) {
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
