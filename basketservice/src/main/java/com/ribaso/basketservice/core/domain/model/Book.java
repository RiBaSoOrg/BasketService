package com.ribaso.basketservice.core.domain.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Book {
    private String id;
    private String title;
    private BigDecimal price;


 @JsonCreator
    public Book(@JsonProperty("id") String id,
    @JsonProperty("title") String title,
    @JsonProperty("price") BigDecimal price){
        this.id = id; 
        this.title = title;
        this.price = price;
    }


public Book() {
    //TODO Auto-generated constructor stub
}
}


