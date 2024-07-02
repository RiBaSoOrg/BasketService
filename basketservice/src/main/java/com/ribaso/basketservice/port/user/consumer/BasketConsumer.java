package com.ribaso.basketservice.port.user.consumer;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.core.domain.service.interfaces.BasketRepository;
import com.ribaso.basketservice.core.domain.service.interfaces.ItemRepository;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.math.BigDecimal;

@Service
public class BasketConsumer {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ItemRepository itemRepository;

    @RabbitListener(queues = "basketQueue")
    public void receiveMessage(String message) {
        // Nachricht parsen, um Basket ID, Item ID und neue Preise zu extrahieren
        String[] parts = message.split(",");
        String basketID = parts[0];
        String itemID = parts[1];
        int amount = Integer.parseInt(parts[2]);
        BigDecimal price = new BigDecimal(parts[3]);

        // Basket und Item aus der Datenbank laden oder neu erstellen
        Basket basket = basketRepository.findById(basketID)
                .orElseThrow(() -> new UnknownBasketIDException("Basket not found"));
        Item item = itemRepository.findById(itemID)
                .orElseThrow(() -> new UnknownItemIDException("Item not found"));

        // Item im Warenkorb aktualisieren
        item.setAmount(amount);
        item.setPrice(price);
        itemRepository.save(item);
    }
}
