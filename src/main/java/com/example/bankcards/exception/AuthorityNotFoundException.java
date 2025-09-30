package com.example.bankcards.exception;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException() {
        super("Authority Not Found");
    }

    public AuthorityNotFoundException(String msg) {
        super(msg);
    }
}
