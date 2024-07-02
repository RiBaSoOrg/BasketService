package com.ribaso.basketservice.port.exception;

public class UnknownBasketIDException extends RuntimeException {
    public UnknownBasketIDException(String message) {
        super(message);
    }
}