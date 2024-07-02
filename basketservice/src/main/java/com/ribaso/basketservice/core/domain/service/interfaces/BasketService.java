package com.ribaso.basketservice.core.domain.service.interfaces;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;

import java.math.BigDecimal;
import java.util.List;

public interface BasketService {
    Basket getBasket(String basketID);
    boolean removeBasket(String basketID);
    BigDecimal getTotalCosts(String basketID);
    String getBasketID(String userID);
    Item getItem(String basketID, String itemID);
    boolean addItem(String basketID, String itemID, int amount);
    boolean removeItem(String basketID, String itemID, int amount);
}
