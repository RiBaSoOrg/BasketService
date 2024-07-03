package com.ribaso.basketservice.core.domain.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Book {
    private String id;
    private String title;
    private BigDecimal price;
}
