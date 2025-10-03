package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа с балансом карты")
public class BalanceResponse {

    @Schema(description = "Баланс карты", example = "1234.56")
    private BigDecimal balance;
}
