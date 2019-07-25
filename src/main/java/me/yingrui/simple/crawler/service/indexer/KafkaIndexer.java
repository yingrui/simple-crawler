package me.yingrui.simple.crawler.service.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;
import java.util.Properties;

public class KafkaIndexer implements Indexer {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Producer<String, String> procuder;
    String topic;

    public KafkaIndexer(String brokers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 10);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        procuder = new KafkaProducer<>(props);
    }

    @Override
    public String getType() {
        return "kafka";
    }

    @Override
    public void index(String key, Map<String, Object> obj) {
        String value;
        try {
            value = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        procuder.send(new ProducerRecord<>(topic, key, value));
    }

    @Override
    public void close() {
        procuder.close();
    }
}
