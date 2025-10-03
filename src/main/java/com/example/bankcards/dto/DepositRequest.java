package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для пополнения карты")
public class DepositRequest {

    @Schema(description = "Номер карты", example = "1234567812345678")
    @NotBlank(message = "Номер карты не может быть пустым")
    private String cardNumber;

    @Schema(description = "Сумма пополнения", example = "500.00")
    @NotNull(message = "Сумма пополнения обязательна")
    @DecimalMin(value = "0.01", message = "Сумма пополнения должна быть больше 0")
    private BigDecimal amount;
}
