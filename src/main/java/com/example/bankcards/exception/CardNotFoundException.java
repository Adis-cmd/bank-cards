package com.example.bankcards.exception;

import java.util.NoSuchElementException;

public class CardNotFoundException extends NoSuchElementException {
    public CardNotFoundException(String message) {
        super(message);
    }
    public CardNotFoundException() {
        super("Card not found!!");
    }
}
