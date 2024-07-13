package com.ribaso.basketservice.port.basket.consumer;

import com.ribaso.basketservice.core.domain.model.Book;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.ConcurrentHashMap;
import org.springframework.amqp.core.Message;


@Component
public class ResponseListener {
    private static final Logger log = LoggerFactory.getLogger(Book.class);


    private ConcurrentHashMap<String, Book> responseMap = new ConcurrentHashMap<>();

    @RabbitListener(queues = "bookResponseQueue")
    public void receiveBookDetails(Book book, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId != null && responseMap.containsKey(correlationId)) {
            responseMap.put(correlationId, book);
            // Hier können Sie weitere Verarbeitungen vornehmen, z.B. eine Callback-Funktion aufrufen
        }
    }

    public void registerCorrelationId(String correlationId) {
        if (correlationId != null) {
            responseMap.put(correlationId, null);
        } else {
            // Log an error or throw an exception
            log.error("Attempted to register a null correlation ID");
        }
    }

    public Book getResponseForCorrelationId(String correlationId) {
        return responseMap.get(correlationId);
    }
}