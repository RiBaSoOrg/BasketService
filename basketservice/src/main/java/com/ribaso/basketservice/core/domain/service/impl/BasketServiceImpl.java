package com.ribaso.basketservice.core.domain.service.impl;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Book;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.basket.consumer.ResponseListener;
import com.ribaso.basketservice.port.basket.producer.SendBookId;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private SendBookId sendBookId;
    
    @Autowired
    private ResponseListener responseListener;

    private Book waitForBookDetails(String correlationId) {
        // Simple blocking loop with a timeout
        final long startTime = System.currentTimeMillis();
        long timeout = 10000; // 10 seconds timeout
        while (System.currentTimeMillis() - startTime < timeout) {
            Book response = responseListener.getResponseForCorrelationId(correlationId);
            if (response != null) {
                return response;
            }
            try {
                Thread.sleep(100);  // Sleep a little to avoid hogging CPU
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null; // Return null if timeout reached
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
            String correlationId = UUID.randomUUID().toString();  // Generate a unique ID
            sendBookId.sendBookId(itemID, correlationId);  // Assume sendBookId is properly set up to send messages
            responseListener.registerCorrelationId(correlationId);  // Register correlation ID to wait for the response
    
            Book book = waitForBookDetails(correlationId);  // Method to wait for the response to be populated
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
