package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "User Controller", description = "Операции пользователя по управлению картами")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;
    private final CardService cardService;

    @Operation(
            summary = "Создание новой карты",
            description = "Создает новую банковскую карту для текущего аутентифицированного пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Карта успешно создана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный запрос или превышен лимит карт"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @PostMapping("/create")
    public ResponseEntity<String> createCard(
            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal) {
        cardService.createCard(principal.getName());
        String msg = "Карта была успешно созданно";
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Получить список моих карт",
            description = "Возвращает все карты текущего пользователя с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список карт успешно получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @GetMapping
    public ResponseEntity<List<CardDto>> getUserCards(
            @Parameter(
                    description = "Параметры пагинации и сортировки",
                    example = "{\n  \"page\": 0,\n  \"size\": 10,\n  \"sort\": \"createdDate,desc\"\n}"
            )
            Pageable pageable,

            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal) {

        List<CardDto> cards = cardService.getUserCards(pageable, principal.getName())
                .getContent();
        return ResponseEntity.ok(cards);
    }

    @Operation(
            summary = "Запрос на блокировку карты",
            description = "Позволяет пользователю отправить запрос на блокировку своей карты. Для подтверждения блокировки требуется действие администратора."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос на блокировку успешно отправлен",
                    content = @Content(schema = @Schema(example = "Запрос на блокировку успешно отправлен"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Карта уже заблокирована или запрос уже отправлен"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не имеет доступа к данной карте"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @PostMapping("/request-block")
    public ResponseEntity<String> requestBlock(
            @Parameter(description = "ID карты для блокировки", required = true, example = "1")
            @RequestParam Long cardId,

            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal) {

        cardService.requestBlock(principal.getName(), cardId);
        return ResponseEntity.ok("Запрос на блокировку успешно отправлен");
    }
}