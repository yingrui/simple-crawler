package me.yingrui.simple.crawler.configuration.properties;


import com.google.common.collect.Lists;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "indexer")
public class IndexerSettings {

    private String clusterNodes;
    private String clusterName;
    private String index;
    private String type;
    private String stringFields;

    public List<TransportAddress> getClusterNodes() {
        ArrayList<String> listIpAndPort = Lists.newArrayList(clusterNodes.split(","));

        return Lists.transform(listIpAndPort, ipPort -> {
            String[] split = ipPort.split(":");
            String ip = split[0];
            int port = Integer.valueOf(split[1]);
            try {
                return new TransportAddress(InetAddress.getByName(ip), port);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getStringFields() {
        return Lists.newArrayList(stringFields.split(",")).stream().map(f -> f.trim()).collect(Collectors.toList());
    }

    public void setStringFields(String stringFields) {
        this.stringFields = stringFields;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
