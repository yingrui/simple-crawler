package me.yingrui.simple.crawler.dao;

import me.yingrui.simple.crawler.model.WebLink;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class WebLinkRepository {

    private HbaseTemplate hbaseTemplate;
    private String tableName = "links";
    private String columnFamilyName = "crawler";

    public WebLinkRepository(HbaseTemplate hbaseTemplate, HBaseAdmin hBaseAdmin) throws IOException {
        this.hbaseTemplate = hbaseTemplate;

        if (!hBaseAdmin.isTableAvailable(tableName)) {
            TableName name = TableName.valueOf(tableName);
            HTableDescriptor desc = new HTableDescriptor(name);
            HColumnDescriptor columnFamily = new HColumnDescriptor(columnFamilyName.getBytes());
            desc.addFamily(columnFamily);
            hBaseAdmin.createTable(desc);
        }
    }

    private void put(String row, String qualifier, String value) {
        try {
            if (isNotEmpty(value)) {
                hbaseTemplate.put(tableName, row, columnFamilyName, qualifier, value.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void save(WebLink webLink) {
        put(webLink.getRowKey(), "url", webLink.getUrl());
        put(webLink.getRowKey(), "website", webLink.getWebsite());
        put(webLink.getRowKey(), "content", webLink.getContent());
        put(webLink.getRowKey(), "contentType", webLink.getContentType());
        put(webLink.getRowKey(), "createTime", String.valueOf(webLink.getCreateTime()));
        put(webLink.getRowKey(), "parentUrl", webLink.getParentUrl());
        put(webLink.getRowKey(), "depth", String.valueOf(webLink.getDepth()));
    }
}
