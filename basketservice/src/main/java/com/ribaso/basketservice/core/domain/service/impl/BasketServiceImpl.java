package com.ribaso.basketservice.core.domain.service.impl;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Book;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.basket.producer.GetBookDetails;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    
    private static final Logger log = LoggerFactory.getLogger(Book.class);

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private GetBookDetails getBookDetails;
    
    

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
           
            Book book = (Book) getBookDetails.getBookDetails(itemID);  // Method to wait for the response to be populated
            if (book == null) {
                throw new UnknownItemIDException("Book not found");
            }

            Item newItem = new Item();
            log.info("itemId"+itemID);
            newItem.setId(itemID);
            log.info("amount"+ amount);
            newItem.setAmount(amount);
            log.info("bookTitle"+ book.getTitle());
            newItem.setName(book.getTitle());
            log.info("Price_string"+ (book.getPrice()));
            try {
                newItem.setPrice(cleanAndConvertToBigDecimal(book.getPrice()));
            }
            catch (NumberFormatException e) {
                log.error("Invalid number format for price: " + book.getPrice(), e);
            }
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
            basket.getItems().remove(item);
            itemRepository.delete(item);
        } else {
            itemRepository.save(item);
        }
    
        basketRepository.save(basket); // Save the basket after modifying items
        return true;
    }

    @Override
    @Transactional
    public Basket createBasket(String userId) {
        Basket basket = new Basket();
        basket.setId(userId);
        basket.setUserId(userId);
        return basketRepository.save(basket);
    }

    public BigDecimal cleanAndConvertToBigDecimal(String price) {
        if (price == null) {
            return BigDecimal.ZERO; // oder werfe eine Exception, wenn das angemessener ist
        }
        
        // Ersetzt alles, was keine Ziffer, kein Punkt oder kein Minuszeichen ist
        String cleanedPrice = price.replaceAll("[^\\d.-]", "");
        
        try {
            return new BigDecimal(cleanedPrice);
        } catch (NumberFormatException e) {
            // Logge den Fehler oder handle ihn entsprechend
            System.err.println("Fehler beim Umwandeln des Preises: " + e.getMessage());
            return BigDecimal.ZERO; // oder werfe eine Exception
        }
    }

}
