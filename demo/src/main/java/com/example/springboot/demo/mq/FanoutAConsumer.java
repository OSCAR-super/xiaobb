package com.example.springboot.demo.mq;

import com.example.springboot.demo.service.UserService;
import com.example.springbootdemoentity.entity.Product;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "fanout.a")
public class FanoutAConsumer {
    @Autowired
    private UserService userService;
    /**
     * 消息消费
     * @RabbitHandler 代表此方法为接受到消息后的处理方法
     */
    @RabbitHandler
    public void recieved(String Id) {
        System.out.println("[fanout.a] recieved message: " + Id);


    }
}