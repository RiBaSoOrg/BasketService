package com.ribaso.basketservice.port.user.controller;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import com.ribaso.basketservice.port.exception.BasketNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PostMapping
    public ResponseEntity<Basket> createBasket(@RequestParam String userId) {
        Basket basket = basketService.createBasket(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(basket);
    }
    
    @GetMapping("/{basketID}")
    public ResponseEntity<Basket> getBasket(@PathVariable String basketID) {
        try {
            return ResponseEntity.ok(basketService.getBasket(basketID));
        } catch (BasketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{basketID}")
    public ResponseEntity<Boolean> removeBasket(@PathVariable String basketID) {
        return ResponseEntity.ok(basketService.removeBasket(basketID));
    }

    @GetMapping("/{basketID}/total-costs")
    public ResponseEntity<BigDecimal> getTotalCosts(@PathVariable String basketID) {
        return ResponseEntity.ok(basketService.getTotalCosts(basketID));
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<String> getBasketID(@PathVariable String userID) {
        return ResponseEntity.ok(basketService.getBasketID(userID));
    }

    @GetMapping("/{basketID}/items/{itemID}")
    public ResponseEntity<Item> getItem(@PathVariable String basketID, @PathVariable String itemID) {
        return ResponseEntity.ok(basketService.getItem(basketID, itemID));
    }

    @PostMapping("/{basketID}/items")
    public ResponseEntity<Boolean> addItem(@PathVariable String basketID, @RequestParam String itemID, @RequestParam int amount) {
        return ResponseEntity.ok(basketService.addItem(basketID, itemID, amount));
    }

    @DeleteMapping("/{basketID}/items/{itemID}")
    public ResponseEntity<Boolean> removeItem(@PathVariable String basketID, @PathVariable String itemID, @RequestParam int amount) {
        return ResponseEntity.ok(basketService.removeItem(basketID, itemID, amount));
    }
}
