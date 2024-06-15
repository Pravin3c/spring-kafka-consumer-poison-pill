package dev.pravin.spring.kafka.avro.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import dev.pravin.avro.product.Product;

@Slf4j
@Component
public class ProductAvroProducer {

    static final String TOPIC_NAME = "product-data-avro";

    private final KafkaTemplate<String, Product> kafkaTemplate;

    public ProductAvroProducer(KafkaTemplate<String, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(Product product) {
        log.info("Producing Product Data: {}, {}, {} in topic {}", product.getProductId(),
                product.getName(), product.getDescription(), TOPIC_NAME);
        kafkaTemplate.send(TOPIC_NAME, String.valueOf(product.getProductId()), product);
    }
}
