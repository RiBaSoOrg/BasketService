package com.ribaso.basketservice.core.domain.service.interfaces;

import com.ribaso.basketservice.core.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, String> {
}
