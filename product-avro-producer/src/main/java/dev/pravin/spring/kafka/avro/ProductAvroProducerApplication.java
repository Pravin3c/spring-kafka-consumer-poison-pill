package dev.pravin.spring.kafka.avro;

import dev.pravin.avro.product.Product;
import dev.pravin.spring.kafka.avro.producer.ProductAvroProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ProductAvroProducerApplication {

    @Autowired
    ProductAvroProducer productAvroProducer;

    public static void main(String[] args) {
        System.out.println("Inside Producer main");
        SpringApplication.run(ProductAvroProducerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void produce() {

        System.out.println("Inside Produce method");
        productAvroProducer.produce(Product.newBuilder().
                setProductId(1).
                setName("Bread").
                setDescription("This is a Bread Product")
                .build()
        );
    }
}
