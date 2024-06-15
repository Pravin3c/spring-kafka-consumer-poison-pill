
# Spring Kafka Avro Example

A sample springboot project using spring-kafka and avro as message template.



## Using

* Java 17
* Spring Boot 3.2.5
* Confluent Kafka 7.6.1
* Confluent Schema Registry 7.6.1
* Avro 1.11.3
* Spring Kafka 3.1.4
## Modules

1) `product-avro-model`
   Module for Avro files and Java Generated Code using avro maven plugin

2) `product-avro-producer`
   Sample Spring Kafka Avro Producer

3) `product-avro-consumer`
   Spring Kafka Avro Consumer


## Goal

Goal of this example project is to show the kafka producer and consumer using avro as a message template.

* `master` branch - complete code example

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
