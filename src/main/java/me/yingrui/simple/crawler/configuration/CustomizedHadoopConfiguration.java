package me.yingrui.simple.crawler.configuration;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.config.annotation.EnableHadoop;
import org.springframework.data.hadoop.config.annotation.SpringHadoopConfigurerAdapter;
import org.springframework.data.hadoop.hbase.HbaseConfigurationFactoryBean;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.IOException;

@Configuration
@EnableHadoop
public class CustomizedHadoopConfiguration extends SpringHadoopConfigurerAdapter {

    @Value("${spring.zookeeper.quorum:}")
    String zkQuorum;

    @Value("${spring.zookeeper.port:2181}")
    int zkPort;

    @Bean
    public HbaseConfigurationFactoryBean hbaseConfigurationFactoryBean(org.apache.hadoop.conf.Configuration conf) {
        HbaseConfigurationFactoryBean hbaseConfigurationFactoryBean = new HbaseConfigurationFactoryBean();
        hbaseConfigurationFactoryBean.setConfiguration(conf);
        hbaseConfigurationFactoryBean.setZkQuorum(zkQuorum);
        hbaseConfigurationFactoryBean.setZkPort(zkPort);
        hbaseConfigurationFactoryBean.setDeleteConnection(false);
        return hbaseConfigurationFactoryBean;
    }

    @Bean("hbaseTemplate")
    public HbaseTemplate hbaseTemplate(HbaseConfigurationFactoryBean hbaseConfigurationFactoryBean) {
        org.apache.hadoop.conf.Configuration config = hbaseConfigurationFactoryBean.getObject();

        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(config);

        return hbaseTemplate;
    }

    @Bean
    public HBaseAdmin hBaseAdmin(HbaseConfigurationFactoryBean hbaseConfigurationFactoryBean) throws IOException, ServiceException {
        org.apache.hadoop.conf.Configuration config = hbaseConfigurationFactoryBean.getObject();
        HBaseAdmin.checkHBaseAvailable(config);

        return new HBaseAdmin(config);
    }
}
