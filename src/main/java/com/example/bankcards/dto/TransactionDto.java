package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для транзакции")
public class TransactionDto {

    @Schema(description = "Номер карты отправителя", example = "**** **** **** 1234")
    private String fromCardNumber;

    @Schema(description = "Номер карты получателя", example = "**** **** **** 5678")
    private String toCardNumber;

    @Schema(description = "Дата и время транзакции", example = "2025-10-03T14:30:00")
    private LocalDateTime dateTime;

    @Schema(description = "Статус транзакции", allowableValues = {"PENDING", "SUCCESS", "FAILED"}, example = "SUCCESS")
    private TransactionStatus status;

    @Schema(description = "Сумма транзакции", example = "250.75")
    private BigDecimal amount;
}
