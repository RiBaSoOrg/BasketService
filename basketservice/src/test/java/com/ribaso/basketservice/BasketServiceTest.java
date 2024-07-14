package com.ribaso.basketservice;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Book;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.impl.BasketServiceImpl;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.basket.producer.GetBookDetails;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private GetBookDetails getBookDetails; 

    @InjectMocks
    private BasketServiceImpl basketServiceImpl;

    private BasketService basketService;

    private Basket basket;
    private Item item;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        basketService = basketServiceImpl;

        basket = new Basket();
        basket.setId("1");

        item = new Item();
        item.setId("1");
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.00"));
        item.setAmount(2);
        item.setBasket(basket);

        book = new Book();
        book.setId("1");
        book.setTitle("Test Book");
        book.setPrice(("10.00"));

        basket.setItems(Arrays.asList(item));

    }

    @Test
    void getBasket_ShouldReturnBasket_WhenBasketExists() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));

        Basket result = basketService.getBasket("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void getBasket_ShouldThrowException_WhenBasketDoesNotExist() {
        when(basketRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UnknownBasketIDException.class, () -> basketService.getBasket("1"));
    }

    @Test
    void removeBasket_ShouldReturnTrue_WhenBasketExists() {
        when(basketRepository.existsById("1")).thenReturn(true);
        doNothing().when(basketRepository).deleteById("1");

        boolean result = basketService.removeBasket("1");
        assertTrue(result);

        verify(basketRepository, times(1)).deleteById("1");
    }

    @Test
    void getTotalCosts_ShouldReturnTotalCostOfItems() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));

        BigDecimal totalCosts = basketService.getTotalCosts("1");
        assertEquals(new BigDecimal("20.00"), totalCosts);
    }

    @Test
    void getBasketID_ShouldReturnBasketID_WhenUserExists() {
        when(basketRepository.findByUserId("user1")).thenReturn(Optional.of(basket));

        String basketID = basketService.getBasketID("user1");
        assertEquals("1", basketID);
    }

    @Test
    void getBasketID_ShouldThrowException_WhenUserDoesNotExist() {
        when(basketRepository.findByUserId("user1")).thenReturn(Optional.empty());

        assertThrows(UnknownBasketIDException.class, () -> basketService.getBasketID("user1"));
    }

    @Test
    void getItem_ShouldReturnItem_WhenItemExists() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));

        Item result = basketService.getItem("1", "1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void getItem_ShouldThrowException_WhenItemDoesNotExist() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));

        assertThrows(UnknownItemIDException.class, () -> basketService.getItem("1", "2"));
    }

    @Test
    void addItem_ShouldAddNewItem_WhenItemDoesNotExist() throws Exception {
        book = new Book();
        book.setId("2");
        book.setTitle("Test Book");
        book.setPrice(("10.00"));

        basket.setItems(Arrays.asList(item));

        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(getBookDetails.getBookDetails(anyString())).thenReturn(book);

        // Jackson2JsonMessageConverter im Test verwenden
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        rabbitTemplate.setMessageConverter(converter);

        // Buch als JSON simulieren
        Message responseMessage = converter.toMessage(book, new MessageProperties());

        // Konfigurieren Sie das Mock des RabbitTemplate, um die simulierte Antwort
        // zurückzugeben
        when(rabbitTemplate.sendAndReceive(eq("bookExchange"), eq("bookRoutingKey"), any(Message.class)))
                .thenReturn(responseMessage);

        boolean result = basketService.addItem("1", "2", 3);
        assertTrue(result);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItem_ShouldIncreaseItemAmount_WhenItemExists() throws IOException {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        boolean result = basketService.addItem("1", "1", 3);
        assertTrue(result);
        assertEquals(5, item.getAmount());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addItem_ShouldThrowException_WhenAmountIsInvalid() {
        assertThrows(InvalidAmountException.class, () -> basketService.addItem("1", "1", 0));
    }

    @Test
    void removeItem_ShouldDecreaseItemAmount_WhenItemExists() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        boolean result = basketService.removeItem("1", "1", 1);
        assertTrue(result);
        assertEquals(1, item.getAmount());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void removeItem_ShouldDeleteItem_WhenAmountIsZero() {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));
        doNothing().when(itemRepository).delete(item);

        boolean result = basketService.removeItem("1", "1", 2);
        assertTrue(result);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void removeItem_ShouldThrowException_WhenAmountIsInvalid() {
        assertThrows(InvalidAmountException.class, () -> basketService.removeItem("1", "1", 0));
    }

    @Test
    void createBasket_ShouldCreateAndReturnBasket() {
        Basket newBasket = new Basket();
        newBasket.setId("2");
        newBasket.setUserId("user2");

        when(basketRepository.save(any(Basket.class))).thenReturn(newBasket);

        Basket result = basketService.createBasket("user2");

        assertNotNull(result);
        assertEquals("2", result.getId());
        assertEquals("user2", result.getUserId());

        verify(basketRepository, times(1)).save(any(Basket.class));
    }
}
