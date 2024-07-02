package com.ribaso.basketservice.port.exception;

public class UnknownItemIDException extends RuntimeException {
    public UnknownItemIDException(String message) {
        super(message);
    }
}