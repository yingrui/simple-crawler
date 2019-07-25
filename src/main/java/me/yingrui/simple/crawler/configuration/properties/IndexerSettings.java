package me.yingrui.simple.crawler.configuration.properties;


import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IndexerSettings {

    @Getter
    @Setter
    private String indexerType;
    @Setter
    private String clusterNodes;
    @Getter
    @Setter
    private String clusterName;
    @Getter
    @Setter
    private String index;
    @Getter
    @Setter
    private String type;
    @Setter
    private String stringFields;

    public String getPlainNodes() {
        return this.clusterNodes;
    }

    public List<TransportAddress> getClusterNodes() {
        ArrayList<String> listIpAndPort = Lists.newArrayList(clusterNodes.split(","));

        return Lists.transform(listIpAndPort, ipPort -> {
            String[] split = ipPort.split(":");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);
            try {
                return new TransportAddress(InetAddress.getByName(ip), port);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> getStringFields() {
        return Lists.newArrayList(stringFields.split(",")).stream().map(String::trim).collect(Collectors.toList());
    }
}
