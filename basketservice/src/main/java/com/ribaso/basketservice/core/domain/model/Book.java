package com.ribaso.basketservice.core.domain.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Data
@NoArgsConstructor
public class Book {
    @Id
    private String id;
    private String title;
    private String subtitle;
    private String isbn;
    private String abstractText;
    private String author;
    private String publisher;
    private String price;
    private int numPages;

    @JsonCreator // This annotation helps in guiding Jackson how to construct an instance of Book
    public Book(@JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("subtitle") String subtitle,
            @JsonProperty("isbn") String isbn,
            @JsonProperty("abstractText") String abstractText,
            @JsonProperty("author") String author,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("price") String price,
            @JsonProperty("numPages") int numPages) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.isbn = isbn;
        this.abstractText = abstractText;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.numPages = numPages;
    }

}
