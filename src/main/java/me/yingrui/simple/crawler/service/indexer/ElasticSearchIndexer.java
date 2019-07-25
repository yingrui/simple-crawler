package me.yingrui.simple.crawler.service.indexer;

import me.yingrui.simple.crawler.configuration.properties.IndexerSettings;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.Map;

import static me.yingrui.simple.crawler.util.JsonUtils.toJson;

public class ElasticSearchIndexer implements Indexer {

    private final String index;
    private final TransportClient client;
    private final String type;


    public ElasticSearchIndexer(IndexerSettings indexerSettings, TransportClient transportClient) {
        this.client = transportClient;
        this.index = indexerSettings.getIndex();
        this.type = indexerSettings.getType();
    }

    @Override
    public String getType() {
        return "ElasticSearch";
    }

    @Override
    public void index(String key, Map<String, Object> obj) {
        String json = toJson(obj);
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(json, XContentType.JSON)
                .setId((String) obj.get("url"))
                .get();
    }

    @Override
    public void close() {
        client.close();
    }
}
