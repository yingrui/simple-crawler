package me.yingrui.simple.crawler.dao;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

    public void put(String row, String family, String qualifier, String value) throws UnsupportedEncodingException {
        hbaseTemplate.put(tableName, row, family, qualifier, value.getBytes("UTF-8"));
    }
}
