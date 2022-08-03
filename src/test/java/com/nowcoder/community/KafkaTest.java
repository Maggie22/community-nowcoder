package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class KafkaTest {

    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void kafkaTest(){
        kafkaProducer.senMessage("test", "你好！");
        kafkaProducer.senMessage("test", "能收到吗");

        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void senMessage(String title, String content){
        kafkaTemplate.send(title, content);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}