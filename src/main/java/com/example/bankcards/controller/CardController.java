package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

@Tag(name = "Cards Controller", description = "Общие операции с картами (доступны пользователям и администраторам)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Operation(
            summary = "Получить баланс карты",
            description = "Возвращает текущий баланс указанной карты. Доступно владельцу карты и администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Баланс успешно получен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к карте запрещен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> viewBalance(
            @Parameter(description = "ID карты", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal) {

        BigDecimal balance = cardService.viewBalance(id, principal.getName())
                .setScale(2, RoundingMode.HALF_UP);
        return ResponseEntity.ok(new BalanceResponse(balance));
    }

    @Operation(
            summary = "Получить информацию о карте",
            description = "Возвращает полную информацию о карте включая номер, баланс, статус и данные владельца. Доступно владельцу карты и администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о карте успешно получена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ к карте запрещен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(
            @Parameter(description = "ID карты", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal) {

        CardDto cardDto = cardService.getCardById(id, principal.getName());
        return ResponseEntity.ok(cardDto);
    }
}