package com.ribaso.basketservice.core.domain.service.impl;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Basket getBasket(String basketID) {
        return basketRepository.findById(basketID).orElseThrow(() -> new UnknownBasketIDException("Basket not found"));
    }

    @Override
    public boolean removeBasket(String basketID) {
        Basket basket = getBasket(basketID);
        basketRepository.delete(basket);
        return true;
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
    public boolean addItem(String basketID, String itemID, int amount) {
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
            Item newItem = new Item();
            newItem.setId(itemID);
            newItem.setAmount(amount);
            newItem.setBasket(basket);
            itemRepository.save(newItem);
        }
        return true;
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
}
