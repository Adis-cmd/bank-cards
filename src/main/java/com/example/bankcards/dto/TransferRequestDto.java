package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для перевода между картами")
public class TransferRequestDto {

    @Schema(description = "Номер карты отправителя", example = "1234567812345678")
    @NotBlank(message = "Номер карты отправителя обязателен")
    private String fromCardNumber;

    @Schema(description = "Номер карты получателя", example = "8765432187654321")
    @NotBlank(message = "Номер карты получателя обязателен")
    private String toCardNumber;

    @Schema(description = "Сумма перевода", example = "1000.50")
    @NotNull(message = "Сумма перевода обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
