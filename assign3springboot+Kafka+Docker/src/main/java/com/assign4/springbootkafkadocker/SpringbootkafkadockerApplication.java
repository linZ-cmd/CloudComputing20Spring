package com.assign4.springbootkafkadocker;

import com.assign4.springbootkafkadocker.producer.Producer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringbootkafkadockerApplication {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(SpringbootkafkadockerApplication.class, args);
            Producer sender = app.getBean(Producer.class);
            sender.sendMessage(args[0], args[1], args[2]);
            Thread.sleep(200);
    }
}
