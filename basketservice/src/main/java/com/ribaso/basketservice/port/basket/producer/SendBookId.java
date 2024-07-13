package com.ribaso.basketservice.port.basket.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribaso.basketservice.core.domain.model.Book;

@Service
public class SendBookId {

    private static final Logger log = LoggerFactory.getLogger(Book.class);
    
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public SendBookId(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookId(String bookId, String correlationId) {
        log.info("Sending book ID: {}", bookId);
        MessageProperties props = new MessageProperties();
        props.setCorrelationId(correlationId);
        log.info("Sending book ID with correlationId: {}", correlationId);
        log.info("Sending message...");
        Message message = new Message(bookId.getBytes(), props);
        rabbitTemplate.send("bookExchange", "bookRoutingKey", message);
    }
}
