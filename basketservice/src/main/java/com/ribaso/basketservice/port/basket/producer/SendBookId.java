package com.ribaso.basketservice.port.basket.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class SendBookId {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendBookId(String bookId) {
        rabbitTemplate.convertAndSend("bookExchange", "bookRoutingKey", bookId, message -> {
            message.getMessageProperties().setContentType("application/json");
            return message;
        });
    }
    
}
