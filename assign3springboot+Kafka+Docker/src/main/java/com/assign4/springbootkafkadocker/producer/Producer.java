package com.assign4.springbootkafkadocker.producer;

import com.assign4.springbootkafkadocker.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;

import java.util.Date;

@Component
public class Producer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    private Gson gson = new Gson();

    public void sendMessage(String object, String name, String id) throws InterruptedException {
        Message m = new Message();
        String msg = "Object: " + object + ", Name: " + name + ", User ID: " + id;
        m.setId(System.currentTimeMillis());
        m.setMsg(msg);
        m.setSendTime(new Date());
        System.out.println("test3 produce message: " + m.getMsg());
        Thread.sleep(1000);
        kafkaTemplate.send("test3", gson.toJson(m));
    }
}