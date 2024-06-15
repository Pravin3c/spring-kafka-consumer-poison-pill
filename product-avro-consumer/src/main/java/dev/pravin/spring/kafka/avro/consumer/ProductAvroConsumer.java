package dev.pravin.spring.kafka.avro.consumer;

import dev.pravin.avro.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;

@Slf4j
@Component
public class ProductAvroConsumer {

    static final String TOPIC_NAME = "product-data-avro";

    @KafkaListener(topics = TOPIC_NAME)
    public void listen(Product product, @Header(RECEIVED_PARTITION) Integer partitionId) {
        log.info("Consumed: {}, {} {} from partition: {}",
                product.getProductId(), product.getName(),
                product.getDescription(), partitionId);
    }
}
