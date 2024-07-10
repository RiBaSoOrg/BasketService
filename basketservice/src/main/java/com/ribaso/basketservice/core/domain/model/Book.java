package com.ribaso.basketservice.core.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
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

}
