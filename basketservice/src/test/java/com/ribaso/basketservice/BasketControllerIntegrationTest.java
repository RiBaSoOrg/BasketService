package com.ribaso.basketservice;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = { BasketServiceApplication.class })
public class BasketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketRepository basketRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private Basket basket;
    private Item item;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        basket.setId("1");

        item = new Item();
        item.setId("1");
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.00"));
        item.setAmount(2);

        basket.setItems(List.of(item));

        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));
        when(basketRepository.existsById("1")).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
    }

    @Test
    void getBasket_ShouldReturnBasket_WhenBasketExists() throws Exception {
        mockMvc.perform(get("/baskets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(basket.getId()));
    }

    @Test
    void getBasket_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/baskets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_ShouldReturnTrue_WhenItemAdded() throws Exception {
        mockMvc.perform(post("/baskets/1/items")
                .param("itemID", "1")
                .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void removeItem_ShouldReturnTrue_WhenItemRemoved() throws Exception {
        mockMvc.perform(delete("/baskets/1/items/1")
                .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getTotalCosts_ShouldReturnTotalCosts() throws Exception {
        mockMvc.perform(get("/baskets/1/total-costs"))
                .andExpect(status().isOk())
                .andExpect(content().string("20.00"));
    }

    @Test
    void getBasketID_ShouldReturnBasketID() throws Exception {
        when(basketRepository.findByUserId("user1")).thenReturn(Optional.of(basket));

        mockMvc.perform(get("/baskets/user/user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        mockMvc.perform(get("/baskets/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    void addItem_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(post("/baskets/999/items")
                .param("itemID", "1")
                .param("amount", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeItem_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/baskets/999/items/1")
                .param("amount", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTotalCosts_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/baskets/999/total-costs"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBasketID_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(basketRepository.findByUserId("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/baskets/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItem_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/baskets/999/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItem_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        when(basketRepository.findById("1")).thenReturn(Optional.of(basket));

        mockMvc.perform(get("/baskets/1/items/999"))
                .andExpect(status().isNotFound());
    }
}
