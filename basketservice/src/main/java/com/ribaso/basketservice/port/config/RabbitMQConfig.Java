package com.ribaso.basketservice.port.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public Queue bookIdQueue() {
        return new Queue("bookIdQueue");
    }

    @Bean
    public Queue bookQueue() {
        return new Queue("bookQueue");
    }

    @Bean
    public Binding bindingId(Queue bookIdQueue, DirectExchange bookExchange) {
        return BindingBuilder.bind(bookIdQueue).to(bookExchange).with("bookRoutingKey");
    }
    
    @Bean
    public Binding binding(Queue bookQueue, DirectExchange bookExchange) {
        return BindingBuilder.bind(bookQueue).to(bookExchange).with("bookRoutingKey");
    }

}