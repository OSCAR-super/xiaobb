package com.example.springboot.demo.mq;

import com.example.springboot.demo.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "topic.b")
public class TopicBConsumer {
    @Autowired
    private UserService userService;
    /**
     * 消息消费
     * @RabbitHandler 代表此方法为接受到消息后的处理方法
     */
    @RabbitHandler
    public void recieved(String msg) {
        System.out.println("[topic.b] recieved message:" + msg);
        userService.setOrder(msg.split("：")[0],msg.split("：")[1],msg.split("：")[2],msg.split("：")[3],msg.split("：")[4],msg.split("：")[5],msg.split("：")[6],msg.split("：")[7],msg.split("：")[8],msg.split("：")[9],msg.split("：")[10]);
    }
}