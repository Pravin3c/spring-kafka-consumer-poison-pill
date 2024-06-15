package dev.pravin.spring.kafka.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductAvroConsumerApplication {

    public static void main(String[] args) {
        System.out.println("Inside Consumer main");
        SpringApplication.run(ProductAvroConsumerApplication.class, args);
    }
}
