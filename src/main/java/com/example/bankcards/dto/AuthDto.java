package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для регистрации/создания пользователя")
public class AuthDto {

    @Schema(description = "Имя пользователя", example = "Даниэль")
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    @NotBlank(message = "Фамилия пользователя не должна быть пустой")
    private String surname;

    @Schema(description = "Email пользователя", example = "your@gmail.com")
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не должно быть пустым")
    private String email;

    @Schema(description = "Пароль пользователя", example = "password123")
    @NotBlank(message = "Пароль не должно быть пустым")
    private String password;
}
