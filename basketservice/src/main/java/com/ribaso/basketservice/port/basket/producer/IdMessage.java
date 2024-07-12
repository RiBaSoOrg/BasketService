package com.ribaso.basketservice.port.basket.producer;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IdMessage(@JsonProperty("text") String bookId) implements Serializable {
}
