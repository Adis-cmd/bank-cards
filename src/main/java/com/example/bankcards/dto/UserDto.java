package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для пользователя")
public class UserDto {

    @Schema(description = "ID пользователя", example = "12")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Даниэль")
    private String name;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String surname;

    @Schema(description = "Email пользователя", example = "your@gmail.com")
    private String email;
}
