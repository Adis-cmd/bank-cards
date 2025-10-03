package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для входа пользователя")
public class LoginRequest {

    @Schema(description = "Email пользователя")
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не должно быть пустым")
    private String email;

    @Schema(description = "Пароль пользователя")
    @NotBlank(message = "Пароль не должно быть пустым")
    private String password;
}
