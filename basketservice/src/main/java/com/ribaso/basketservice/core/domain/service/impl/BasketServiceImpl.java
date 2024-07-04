package com.ribaso.basketservice.core.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Book;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Book getBookDetails(String bookId) throws IOException {
        Message message = MessageBuilder.withBody(bookId.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
        Message responseMessage = rabbitTemplate.sendAndReceive("bookExchange", "bookRoutingKey", message);
        if (responseMessage != null) {
            String jsonResponse = new String(responseMessage.getBody(), StandardCharsets.UTF_8);
            System.out.println("JSON response: " + jsonResponse); // Zum Debuggen
            try {
                return objectMapper.readValue(responseMessage.getBody(), Book.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse book details from response: " + jsonResponse, e);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean addItem(String basketID, String itemID, int amount) throws IOException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Basket basket = getBasket(basketID);
        Optional<Item> existingItem = basket.getItems().stream()
                .filter(item -> item.getId().equals(itemID))
                .findFirst();

        if (existingItem.isPresent()) {
            Item item = existingItem.get();
            item.setAmount(item.getAmount() + amount);
            itemRepository.save(item);
        } else {
            Book book = getBookDetails(itemID);
            if (book == null) {
                throw new UnknownItemIDException("Book not found");
            }

            Item newItem = new Item();
            newItem.setId(itemID);
            newItem.setAmount(amount);
            newItem.setName(book.getTitle());
            newItem.setPrice(new BigDecimal(book.getPrice()));
            newItem.setBasket(basket);

            itemRepository.save(newItem);
        }
        return true;
    }

    @Override
    public Basket getBasket(String basketID) {
        return basketRepository.findById(basketID).orElseThrow(() -> new UnknownBasketIDException("Basket not found"));
    }

    @Override
    public boolean removeBasket(String basketID) {
        if (basketRepository.existsById(basketID)) {
            basketRepository.deleteById(basketID);
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal getTotalCosts(String basketID) {
        Basket basket = getBasket(basketID);
        return basket.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getBasketID(String userID) {
        Optional<Basket> basket = basketRepository.findByUserId(userID);
        return basket.map(Basket::getId).orElseThrow(() -> new UnknownBasketIDException("Basket not found"));
    }

    @Override
    public Item getItem(String basketID, String itemID) {
        Basket basket = getBasket(basketID);
        return basket.getItems().stream()
                .filter(item -> item.getId().equals(itemID))
                .findFirst()
                .orElseThrow(() -> new UnknownItemIDException("Item not found"));
    }

    @Override
    @Transactional
    public boolean removeItem(String basketID, String itemID, int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Basket basket = getBasket(basketID);
        Item item = getItem(basketID, itemID);

        if (item.getAmount() < amount) {
            throw new InvalidAmountException("Not enough items to remove");
        }

        item.setAmount(item.getAmount() - amount);

        if (item.getAmount() == 0) {
            itemRepository.delete(item);
        } else {
            itemRepository.save(item);
        }
        return true;
    }

    @Override
    @Transactional
    public Basket createBasket(String userId) {
        Basket basket = new Basket();
        basket.setUserId(userId);
        return basketRepository.save(basket);
    }
}
