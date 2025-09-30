package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    BigDecimal balance = BigDecimal.ZERO;

    String name;

    String surname;

    String cardNumber;

    LocalDateTime expirationDate;

    CardStatus status = CardStatus.ACTIVE;
}
