package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для карты")
public class CardDto {

    @Schema(description = "Баланс карты", example = "1500.75")
    private BigDecimal balance;

    @Schema(description = "Имя владельца карты", example = "Даниэль")
    private String name;

    @Schema(description = "Фамилия владельца карты", example = "Петров")
    private String surname;

    @Schema(description = "Номер карты (замаскированный)", example = "**** **** **** 4312")
    private String cardNumber;

    @Schema(description = "Срок действия карты", example = "2026-09-30T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "Статус карты", allowableValues = {"ACTIVE", "BLOCKED", "EXPIRED", "BLOCK_REQUESTED"}, example = "ACTIVE")
    private CardStatus status;
}
