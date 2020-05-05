package com.assign4.springbootkafkadocker.consumer;

import com.assign4.springbootkafkadocker.Message;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    private Gson gson = new Gson();

    @KafkaListener(topics = {"test3"})
    public void processMessage(String content) {
        Message m = gson.fromJson(content, Message.class);
        System.out.println("test3--consume message:" + m.getMsg());
    }
}