package com.example.bankproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"bank-transfers"})
class BankProducerApplicationTests {

    @Test
    void contextLoads() {
    }

}
