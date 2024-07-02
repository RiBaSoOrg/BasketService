package com.ribaso.basketservice.core.domain.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
public class Basket {
    @Id
    private String id;
    private String userId;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items;
}
