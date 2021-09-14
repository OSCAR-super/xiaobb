package com.example.springboot.demo.mq;

import com.alibaba.fastjson.JSONObject;
import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RabbitListener(queues = "fanout.b")
public class FanoutBConsumer {

    /**
     * 消息消费
     * @RabbitHandler 代表此方法为接受到消息后的处理方法
     */
    @RabbitHandler
    public void recieved(String Id) {
        System.out.println("[fanout.b] recieved message: " + Id);
        System.out.println("begin");
        try {
            JSONObject json = null;

            ZhenziSmsClient client = new ZhenziSmsClient("https://sms.zhenzikj.com", "100234", "99137d7c-eb29-451a-a6d2-fac69444372b");
            Map<String, Object> params = new HashMap<String, Object>();

            params.put("number", Id.split("-")[1]);
            params.put("templateId", Id.split("-")[0]);
            String result = client.send(params);
            json = JSONObject.parseObject(result);
            Jedis jedis = new Jedis("localhost");
            System.out.println("连接成功");
            //查看服务是否运行
            System.out.println("服务正在运行: "+jedis.ping());
            Date date = new Date();
            String dateString = new SimpleDateFormat("YYYY-mm-DD").format(date);

            if (json.getIntValue("code") != 0) {//发送短信失败
                if (jedis.exists(dateString)){
                    jedis.set(dateString,jedis.get(dateString)+"-"+Id.split("-")[1]);
                }else {
                    jedis.append(dateString,Id.split("-")[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}