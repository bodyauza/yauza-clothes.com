package com.hood.merch.controllers;

import com.hood.merch.models.Offer;
import jakarta.persistence.criteria.CriteriaBuilder;
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
    private KafkaTemplate<Integer, Offer> kafkaTemplate;

    @PostMapping
    public void sendOrder(Integer msgId, Offer offer){
        offer = new Offer();
        ListenableFuture<SendResult<Integer, Offer>> future = (ListenableFuture<SendResult<Integer, Offer>>) kafkaTemplate.send("msg", msgId, offer);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }

}
