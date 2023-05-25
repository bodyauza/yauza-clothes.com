package com.hood.merch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("msg")
public class MsgController {

    //@Autowired
    //private KafkaTemplate<String, String> kafkaTemplate;

    //@PostMapping
    //public void sendOrder(String msgId, String msg){
       // kafkaTemplate.send("msg", msgId, msg);
    //}
}
