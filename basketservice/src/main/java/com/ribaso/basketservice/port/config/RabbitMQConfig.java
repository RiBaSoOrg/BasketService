package com.ribaso.basketservice.port.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange bookExchange() {
        return new DirectExchange("bookExchange");
    }

    @Bean
    public Queue bookQueue() {
        return new Queue("bookQueue");
    }

    @Bean
    public Binding binding(Queue bookQueue, DirectExchange bookExchange) {
        return BindingBuilder.bind(bookQueue).to(bookExchange).with("bookRoutingKey");
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(typeMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper typeMapper() {
        DefaultClassMapper typeMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        // Zuordnung des Typs aus dem 'bookService' zum lokalen 'Book' in
        // 'basketService'
        idClassMapping.put("com.ribaso.bookservice.core.domain.model.Book",
                com.ribaso.basketservice.core.domain.model.Book.class);
        typeMapper.setIdClassMapping(idClassMapping);
        return typeMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

}