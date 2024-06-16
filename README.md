
# Spring Kafka Consumer Poison Pill Example

A sample springboot project using spring-kafka and avro as message template to demonstrate Consumer Poison Pill scenario.



## Using

* Java 17
* Spring Boot 3.2.5
* Confluent Kafka 7.6.1
* Confluent Schema Registry 7.6.1
* Apache Avro 1.11.3
* Spring Kafka 3.1.4
## Modules

1) `product-avro-model`
   Module for Avro files and Java Generated Code using avro maven plugin

2) `product-avro-producer`
   Spring Kafka Avro Producer

3) `product-avro-consumer`
   Spring Kafka Avro Consumer, along with Dead Letter Topic configurations


## Goal

Goal of this example project is to demonstrate Kafka Consumer Poison Pill scenario.

* `master` branch - complete code example with no consumer configuration to avoid poison pill scenario.
* `handle-poison-pill-by-logging` branch - complete code example with consumer configuration to handle poison pill scenario by logging the poison pill and continue consuming.
* `handle-poison-pill-dead-letter-topic` branch - complete code example with consumer configuration to handle poison pill scenario by sending the poison pill to a dead letter topic and continue consuming.

## Build to the project

```
./mvnw clean install
```
## Start / Stop Kafka & Zookeeper

```
docker-compose up -d
```

```
docker-compose down -v
```
## Start both producer and consumer

* Spring Boot application: `product-avro-producer`

```
java -jar product-avro-producer/target/product-avro-producer-0.0.1-SNAPSHOT.jar
```

* Spring Boot application: `product-avro-consumer`

```
java -jar product-avro-consumer/target/product-avro-consumer-0.0.1-SNAPSHOT.jar
```

## Execute the Poison Pill using KafkaCat

Go to the exec mode of the kafkacat service running in the docker container and run the below command to send a poison pill message to the topic.

```
kafkacat -P -b kafka:29092 -t product-data-avro -K:  <ENTER>
<ENTER the poison pill in the next line>

Example:
kafkacat -P -b kafka:29092 -t product-data-avro -K:
This is a poison pill message
```

## How to survive the Poison Pill

Spring Kafka provides support to protect applications from poison pill. There are multiple ways to handle poison pill scenarios.

## Log the poison pill and continue consuming

Branch: `handle-poison-pill-by-logging`

Configuration of the Consumer Application:

```
spring:
  kafka:
    consumer:
      # Configures the Spring Kafka ErrorHandlingDeserializer that delegates to the 'real' deserializers
      key-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
    properties:
      # Delegate deserializers
      spring.deserializer.key.delegate.class: org.apache.kafka.common.serialization.StringDeserializer
      spring.deserializer.value.delegate.class: io.confluent.kafka.serializers.KafkaAvroDeserializer
```

Configure DefaultErrorhandler to log the poison pill record details:

```
@Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                (record, exception) -> {
                    log.info("Error occurred while processing: " + record);
                    log.warn("Exception: " + exception);
                }, new FixedBackOff(0L, 2L) );
    }
```

## Publish the poison pill to a dead letter topic and continue consuming

Branch: `handle-poison-pill-dead-letter-topic`

Configuration of the Consumer Application:

```
spring:
  kafka:
    # The producer configuration
    # The key-serializer is a StringSerializer and the value-serializer is a ByteArraySerializer
    # The consumer becomes th producer since it is publishing the message to the dead letter topic
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.ByteArraySerializer
```

Configure the DeadLetterPublishingRecoverer to publish the poison pill to the dead letter topic:

```
    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        return new DefaultErrorHandler(deadLetterPublishingRecoverer);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Byte[]> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate);
    }
    
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> deadLetterTopicKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deadLetterTopicConsumerFactory());
        return factory;
    }
```

Configure the KafkaListener for Dead Letter Topic:

```
    @KafkaListener(topics = DLT_TOPIC_NAME, containerFactory = "deadLetterTopicKafkaListenerContainerFactory")
    public void listen(Bytes poisonPillData) {
        log.info( "Poison Pill consumed: {}", poisonPillData );
    }
```