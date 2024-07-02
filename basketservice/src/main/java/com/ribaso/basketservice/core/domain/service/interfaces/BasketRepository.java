package com.ribaso.basketservice.core.domain.service.interfaces;

import com.ribaso.basketservice.core.domain.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, String> {
    Optional<Basket> findByUserId(String userId);
}
