package dev.pravin.spring.kafka.avro.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaDltConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaDltConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /*
    SpringBoot will automatically detect this bean and use it as the default error handler for all listeners.
     */
    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        return new DefaultErrorHandler(deadLetterPublishingRecoverer);
    }

    /*
    This bean is used to publish the poison pill to the dead letter topic.
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Byte[]> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate);
    }

    /*

     */
    @Bean
    public Map<String, Object> deadLetterTopicConsumerConfigs() {
        Map<String, Object> consumerConfigs = new HashMap<>();
        consumerConfigs.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerConfigs.put( ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        consumerConfigs.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class );
        consumerConfigs.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return consumerConfigs;
    }

    /*
    This factory is used to create a ConsumerFactory for the dead letter topic.
     */
    @Bean
    public ConsumerFactory<String, Object> deadLetterTopicConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(deadLetterTopicConsumerConfigs());
    }

    /*
    This factory is used to create a KafkaListenerContainerFactory for the dead letter topic.
     */
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> deadLetterTopicKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deadLetterTopicConsumerFactory());
        return factory;
    }
}
