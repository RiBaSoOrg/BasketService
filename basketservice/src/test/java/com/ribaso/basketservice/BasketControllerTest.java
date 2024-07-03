package com.ribaso.basketservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketService;
import com.ribaso.basketservice.port.exception.BasketNotFoundException;
import com.ribaso.basketservice.port.user.advice.GlobalExceptionHandler;
import com.ribaso.basketservice.port.user.controller.BasketController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BasketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BasketService basketService;

    @InjectMocks
    private BasketController basketController;

    private Basket basket;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(basketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        basket = new Basket();
        basket.setId("1");

        item = new Item();
        item.setId("1");
    }

    @Test
    void getBasket_ShouldReturnBasket_WhenBasketExists() throws Exception {
        when(basketService.getBasket("1")).thenReturn(basket);

        mockMvc.perform(get("/baskets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(basket.getId()));
    }

    @Test
    void getBasket_ShouldReturnNotFound_WhenBasketDoesNotExist() throws Exception {
        when(basketService.getBasket("1")).thenThrow(new BasketNotFoundException("Basket not found"));
        
        mockMvc.perform(get("/baskets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeBasket_ShouldReturnTrue_WhenBasketRemoved() throws Exception {
        when(basketService.removeBasket("1")).thenReturn(true);

        mockMvc.perform(delete("/baskets/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getTotalCosts_ShouldReturnTotalCosts() throws Exception {
        BigDecimal totalCosts = new BigDecimal("100.00");
        when(basketService.getTotalCosts("1")).thenReturn(totalCosts);

        mockMvc.perform(get("/baskets/1/total-costs"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalCosts.toString()));
    }

    @Test
    void getBasketID_ShouldReturnBasketID() throws Exception {
        String userID = "1";
        String basketID = "1";
        when(basketService.getBasketID(userID)).thenReturn(basketID);

        mockMvc.perform(get("/baskets/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(basketID));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        when(basketService.getItem("1", "1")).thenReturn(item);

        mockMvc.perform(get("/baskets/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    void addItem_ShouldReturnTrue_WhenItemAdded() throws Exception {
        when(basketService.addItem("1", "1", 1)).thenReturn(true);

        mockMvc.perform(post("/baskets/1/items")
                .param("itemID", "1")
                .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void removeItem_ShouldReturnTrue_WhenItemRemoved() throws Exception {
        when(basketService.removeItem("1", "1", 1)).thenReturn(true);

        mockMvc.perform(delete("/baskets/1/items/1")
                .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
