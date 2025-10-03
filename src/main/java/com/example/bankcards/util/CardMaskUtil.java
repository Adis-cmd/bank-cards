package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CardMaskUtil {

    public String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}