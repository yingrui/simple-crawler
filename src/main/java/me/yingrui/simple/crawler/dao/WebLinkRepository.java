package me.yingrui.simple.crawler.dao;

import me.yingrui.simple.crawler.model.WebLink;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.HbaseUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;

@Service
public class WebLinkRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLinkRepository.class);

    private final HbaseTemplate hbaseTemplate;
    private final String tableName = "links";
    private final String columnFamilyName = "crawler";
    private Table table;

    public WebLinkRepository(HbaseTemplate hbaseTemplate, HBaseAdmin hBaseAdmin) throws IOException {
        this.hbaseTemplate = hbaseTemplate;
        table = getHTable();

        if (!hBaseAdmin.isTableAvailable(tableName)) {
            TableName name = TableName.valueOf(tableName);
            HTableDescriptor desc = new HTableDescriptor(name);
            HColumnDescriptor columnFamily = new HColumnDescriptor(columnFamilyName.getBytes());
            desc.addFamily(columnFamily);
            hBaseAdmin.createTable(desc);
        }
    }

    public boolean exists(String rowKey) {
        Get get = new Get(rowKey.getBytes());
        try {
            return table.exists(get);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WebLink get(String rowKey) {
        Get get = new Get(rowKey.getBytes());
        try {
            Result result = table.get(get);
            return toWebLink(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public void scan(String website, Consumer<WebLink> process) {
        Scan scan = getScan();

        scan.setRowPrefixFilter(toBytes(website));
        try {
            ResultScanner scanner = table.getScanner(scan);
            for (Result r : scanner) {
                String rowKey = new String(r.getRow());
                LOGGER.debug(rowKey);
                WebLink webLink = toWebLink(r);
                process.accept(webLink);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot scan hbase table" + tableName, e);
        }
    }

    public void delete(String website) {
        Scan scan = getScan();

        scan.setRowPrefixFilter(toBytes(website));
        Table table = getHTable();
        try {
            ResultScanner scanner = table.getScanner(scan);
            for (Result r : scanner) {
                String rowKey = new String(r.getRow());
                LOGGER.debug("Delete: " + rowKey);
                hbaseTemplate.delete(tableName, rowKey, columnFamilyName);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot scan hbase table" + tableName, e);
        }
    }

    private WebLink toWebLink(Result r) throws UnsupportedEncodingException {
        String url = getValue(r, "url");
        String website = getValue(r, "website");
        String content = getValue(r, "content");
        String contentType = getValue(r, "contentType");
        Long createTime = Long.valueOf(getValue(r, "createTime"));
        String parentUrl = getValue(r, "parentUrl");
        int depth = Integer.valueOf(getValue(r, "depth"));
        WebLink webLink = new WebLink(url, createTime, content, contentType, parentUrl, depth);
        webLink.setWebsite(website);
        return webLink;
    }

    private String getValue(Result r, String field) throws UnsupportedEncodingException {
        byte[] bytes = r.getValue(toBytes(columnFamilyName), toBytes(field));
        if (bytes == null) {
            return null;
        } else {
            return new String(bytes, "UTF-8");
        }
    }

    private Scan getScan() {
        Scan scan = new Scan();
        scan.addColumn(toBytes(columnFamilyName), toBytes("url"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("website"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("content"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("contentType"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("createTime"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("parentUrl"));
        scan.addColumn(toBytes(columnFamilyName), toBytes("depth"));
        return scan;
    }

    private HTableInterface getHTable() {
        return HbaseUtils.getHTable(tableName, hbaseTemplate.getConfiguration(), hbaseTemplate.getCharset(), hbaseTemplate.getTableFactory());
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
}
