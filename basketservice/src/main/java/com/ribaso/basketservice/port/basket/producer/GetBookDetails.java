package com.ribaso.basketservice.port.basket.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribaso.basketservice.core.domain.model.Book;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;

@Service
public class GetBookDetails {

    private static final Logger log = LoggerFactory.getLogger(Book.class);
    
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public GetBookDetails(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Book getBookDetails(String bookId) {
    log.info("Sending book ID: {}", bookId);
    Book response =  (Book) rabbitTemplate.convertSendAndReceive("exchange", "bookRoutingKey", bookId);
    log.info(bookId, response);
    if (response == null) {
        throw new UnknownItemIDException("Book not found for ID: " + bookId);
    }
    return response;
}

}
