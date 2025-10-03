package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на блокировку/разблокировку карты")
public class BlockCardRequest {

    @NotNull(message = "ID карты обязателен")
    @Positive(message = "ID карты должен быть положительным числом")
    @Schema(description = "Идентификатор карты", example = "1", required = true)
    private Long cardId;
}