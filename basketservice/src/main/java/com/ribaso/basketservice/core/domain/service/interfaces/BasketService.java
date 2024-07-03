package com.ribaso.basketservice.core.domain.service.interfaces;

import com.ribaso.basketservice.core.domain.model.Basket;
import com.ribaso.basketservice.core.domain.model.Item;
import com.ribaso.basketservice.port.exception.InvalidAmountException;
import com.ribaso.basketservice.port.exception.UnknownBasketIDException;
import com.ribaso.basketservice.port.exception.UnknownItemIDException;

import java.math.BigDecimal;
import java.util.List;

public interface BasketService {
    /**
     * Ruft einen Warenkorb anhand seiner ID ab.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @return Der gefundene Warenkorb.
     * @throws UnknownBasketIDException Wenn der Warenkorb nicht gefunden wird.
     */
    Basket getBasket(String basketID);

     /**
     * Entfernt einen Warenkorb anhand seiner ID.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @return true, wenn der Warenkorb erfolgreich entfernt wurde, false andernfalls.
     */
    boolean removeBasket(String basketID);

    /**
     * Berechnet die Gesamtkosten aller Artikel in einem Warenkorb.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @return Die Gesamtkosten aller Artikel im Warenkorb.
     * @throws UnknownBasketIDException Wenn der Warenkorb nicht gefunden wird.
     */
    BigDecimal getTotalCosts(String basketID);

    /**
     * Ruft die ID eines Warenkorbs anhand der Benutzer-ID ab.
     * 
     * @param userID Die eindeutige ID des Benutzers.
     * @return Die ID des Warenkorbs.
     * @throws UnknownBasketIDException Wenn kein Warenkorb für den Benutzer gefunden wird.
     */
    String getBasketID(String userID);

    /**
     * Ruft einen Artikel aus einem Warenkorb ab.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @param itemID Die eindeutige ID des Artikels.
     * @return Der gefundene Artikel.
     * @throws UnknownItemIDException Wenn der Artikel nicht gefunden wird.
     */
    Item getItem(String basketID, String itemID);

    /**
     * Fügt einen Artikel zu einem Warenkorb hinzu.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @param itemID Die eindeutige ID des Artikels.
     * @param amount Die Menge des Artikels, die hinzugefügt werden soll.
     * @return true, wenn der Artikel erfolgreich hinzugefügt wurde, false andernfalls.
     * @throws InvalidAmountException Wenn die Menge ungültig ist.
     * @throws UnknownBasketIDException Wenn der Warenkorb nicht gefunden wird.
     */
    boolean addItem(String basketID, String itemID, int amount);

    /**
     * Entfernt eine bestimmte Menge eines Artikels aus einem Warenkorb.
     * 
     * @param basketID Die eindeutige ID des Warenkorbs.
     * @param itemID Die eindeutige ID des Artikels.
     * @param amount Die Menge des Artikels, die entfernt werden soll.
     * @return true, wenn der Artikel erfolgreich entfernt wurde, false andernfalls.
     * @throws InvalidAmountException Wenn die Menge ungültig ist oder die Menge im Warenkorb nicht ausreicht.
     * @throws UnknownBasketIDException Wenn der Warenkorb nicht gefunden wird.
     * @throws UnknownItemIDException Wenn der Artikel nicht gefunden wird.
     */
    boolean removeItem(String basketID, String itemID, int amount);
}
