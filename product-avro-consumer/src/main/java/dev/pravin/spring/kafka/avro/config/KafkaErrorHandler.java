package dev.pravin.spring.kafka.avro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
@Slf4j
public class KafkaErrorHandler {

    /*
    SpringBoot will automatically detect this bean and use it as the default error handler for all listeners.
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                (record, exception) -> {
                    log.info("Error occurred while processing: " + record);
                    log.error("Exception: " + exception);
                }, new FixedBackOff(0L, 2L) );
    }
}
