package com.ribaso.basketservice.port.shoppingcart.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UpdateProductProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend("updateProductQueue", message);
    }
}
