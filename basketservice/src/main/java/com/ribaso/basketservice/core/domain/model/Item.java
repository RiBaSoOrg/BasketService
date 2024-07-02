package com.ribaso.basketservice.core.domain.model;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Item {
    @Id
    private String id;
    private String name;
    private int amount;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private Basket basket;
}
