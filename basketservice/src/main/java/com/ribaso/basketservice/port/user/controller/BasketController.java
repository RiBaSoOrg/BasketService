package com.ribaso.basketservice.port.user.controller;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import com.ribaso.basketservice.port.exception.BasketNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/baskets")
@CrossOrigin(origins = "http://localhost:3000")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PostMapping
    @Operation(summary = "Create a new basket", description = "Creates a new basket for a specified user.")
    public ResponseEntity<Basket> createBasket(@RequestParam 
    @Parameter(description = "The user ID for whom the basket is created.")
    String userId) {
        Basket basket = basketService.createBasket(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(basket);
    }
    
    @GetMapping("/{basketID}")
    @Operation(summary = "Get a basket", description = "Retrieves a specific basket by its ID. Returns NOT FOUND if the basket does not exist.")
    public ResponseEntity<Basket> getBasket(@PathVariable
    @Parameter(description = "The unique identifier of the basket to retrieve.")
    String basketID) {
        try {
            return ResponseEntity.ok(basketService.getBasket(basketID));
        } catch (BasketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{basketID}")
    @Operation(summary = "Remove a basket", description = "Removes a basket by its ID.")
    public ResponseEntity<Boolean> removeBasket(@PathVariable
    @Parameter(description = "The unique identifier of the basket to remove.")
    String basketID) {
        return ResponseEntity.ok(basketService.removeBasket(basketID));
    }

    @GetMapping("/{basketID}/total-costs")
    @Operation(summary = "Get total costs of a basket", description = "Retrieves the total costs of all items in a specific basket.")
    public ResponseEntity<BigDecimal> getTotalCosts(@PathVariable 
    @Parameter(description = "The unique identifier of the basket.")
    String basketID) {
        return ResponseEntity.ok(basketService.getTotalCosts(basketID));
    }

    @GetMapping("/user/{userID}")
    @Operation(summary = "Get basket ID by user ID", description = "Retrieves the basket ID associated with a user ID.")
    public ResponseEntity<String> getBasketID(@PathVariable 
    @Parameter(description = "The user ID associated with the basket.")
    String userID) {
        return ResponseEntity.ok(basketService.getBasketID(userID));
    }

    @GetMapping("/{basketID}/items/{itemID}")
    @Operation(summary = "Get an item in a basket", description = "Retrieves a specific item from a specified basket.")
    public ResponseEntity<Item> getItem(@PathVariable 
    @Parameter(description = "The user ID associated with the basket.")
    String basketID, @PathVariable 
    @Parameter(description = "The unique identifier of the item to retrieve.")
    String itemID) {
        return ResponseEntity.ok(basketService.getItem(basketID, itemID));
    }

    @PostMapping("/{basketID}/items")
    @Operation(summary = "Add an item to a basket", description = "Adds a specific item to a basket. Amount of the item can be specified.")
    public ResponseEntity<Boolean> addItem(@PathVariable 
    @Parameter(description = "The user ID associated with the basket.")
    String basketID, @RequestParam
    @Parameter(description = "The unique identifier of the item to retrieve.")
    String itemID, @RequestParam 
    @Parameter(description = "The amount of the item to add.")
    int amount) throws IOException {
        return ResponseEntity.ok(basketService.addItem(basketID, itemID, amount));
    }

    @DeleteMapping("/{basketID}/items/{itemID}")
    @Operation(summary = "Remove an item from a basket", description = "Removes a specified amount of an item from a basket.")
    public ResponseEntity<Boolean> removeItem(@PathVariable
    @Parameter(description = "The user ID associated with the basket.")
    String basketID, @PathVariable 
    @Parameter(description = "The unique identifier of the item to retrieve.")
    String itemID, @RequestParam
    @Parameter(description = "The amount of the item to remove.")
    int amount) {
        return ResponseEntity.ok(basketService.removeItem(basketID, itemID, amount));
    }
}
