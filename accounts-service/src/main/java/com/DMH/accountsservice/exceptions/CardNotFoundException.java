package com.DMH.accountsservice.exceptions;

import jakarta.ws.rs.NotFoundException;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
