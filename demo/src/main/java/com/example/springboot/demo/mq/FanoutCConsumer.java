package com.example.springboot.demo.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
@RabbitListener(queues = "fanout.c")
public class FanoutCConsumer {

    /**
     * 消息消费
     * @RabbitHandler 代表此方法为接受到消息后的处理方法
     */
    @RabbitHandler
    public void recieved(String Id) {
        System.out.println("[fanout.c] recieved message: " + Id);

    }
}