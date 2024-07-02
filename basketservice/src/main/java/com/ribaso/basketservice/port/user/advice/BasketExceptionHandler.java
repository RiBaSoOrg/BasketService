package com.ribaso.basketservice.port.user.advice;

import com.ribaso.basketservice.port.exception.BasketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class BasketExceptionHandler {

    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<?> handleBasketNotFoundException(BasketNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Weitere Exception-Handler können hier hinzugefügt werden
}
