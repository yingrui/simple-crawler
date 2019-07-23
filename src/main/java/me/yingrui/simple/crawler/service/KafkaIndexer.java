package me.yingrui.simple.crawler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.yingrui.simple.crawler.model.WebLink;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaIndexer {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Producer<String, String> procuder;
    String topic = "crawler";

    public KafkaIndexer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "twdp-dn5:9092,twdp-dn4:9092,twdp-dn3:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 10);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        procuder = new KafkaProducer<>(props);
    }

    public void index(WebLink webLink) {
        String value;
        try {
            value = objectMapper.writeValueAsString(webLink);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        procuder.send(new ProducerRecord<>(topic, value));
    }

    public void close() {
        procuder.close();
    }
}
