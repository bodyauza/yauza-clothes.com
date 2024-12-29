package com.yauza.clothes.controllers;

import com.yauza.clothes.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class MsgController {

    @Autowired
    private KafkaTemplate<Integer, Order> kafkaTemplate;

    @PostMapping
    public void sendOrder(Integer msgId, Order order){
        order = new Order();
        ListenableFuture<SendResult<Integer, Order>> future = (ListenableFuture<SendResult<Integer, Order>>) kafkaTemplate.send("msg", msgId, order);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }

}
