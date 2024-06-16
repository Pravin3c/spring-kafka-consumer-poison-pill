package dev.pravin.spring.kafka.avro.consumer;

import dev.pravin.avro.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;

@Slf4j
@Component
public class ProductAvroConsumer {

    static final String TOPIC_NAME = "product-data-avro";
    static final String DLT_TOPIC_NAME = "product-data-avro.DLT";

    @KafkaListener(topics = TOPIC_NAME)
    public void listen(Product product, @Header(RECEIVED_PARTITION) Integer partitionId) {
        log.info("Consumed: {}, {} {} from partition: {}",
                product.getProductId(), product.getName(),
                product.getDescription(), partitionId);
    }

    @KafkaListener(topics = DLT_TOPIC_NAME, containerFactory = "deadLetterTopicKafkaListenerContainerFactory")
    public void listen(Bytes poisonPillData) {
        log.info( "Poison Pill consumed: {}", poisonPillData );
    }
}
