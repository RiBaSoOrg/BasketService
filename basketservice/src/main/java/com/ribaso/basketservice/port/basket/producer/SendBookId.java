package com.ribaso.basketservice.port.basket.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.ribaso.basketservice.core.domain.model.Book;

public class SendBookId {

    private static final Logger log = LoggerFactory.getLogger(Book.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public SendBookId(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookId(String bookId) {
        log.info("Sending message...");
        rabbitTemplate.convertAndSend("bookExchange", "bookRoutingKey", bookId);
        }
    }
    
