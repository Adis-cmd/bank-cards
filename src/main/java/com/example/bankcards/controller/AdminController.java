package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.dto.BlockCardRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Controller", description = "Административные операции (управление пользователями, картами и транзакциями)")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CardService cardService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей системы с поддержкой пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей успешно получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для доступа к ресурсу"
            )
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(
            @Parameter(
                    description = "Параметры пагинации и сортировки",
                    example = "{\n  \"page\": 0,\n  \"size\": 20,\n  \"sort\": \"id,asc\"\n}"
            )
            Pageable pageable) {
        List<UserDto> users = userService.getAllUser(pageable).getContent();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Создание нового пользователя",
            description = "Администратор создает нового пользователя в системе. Email должен быть уникальным."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(example = "Пользователь успешно создан"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные пользователя"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для выполнения операции"
            )
    })
    @PostMapping("/users")
    public ResponseEntity<String> createUser(
            @Parameter(description = "Данные для регистрации пользователя", required = true)
            @Valid @RequestBody AuthDto userDto) {
        userService.register(userDto);
        String msg = "Пользователь успешно создан";
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }

    @Operation(
            summary = "Подтверждение блокировки карты",
            description = "Администратор подтверждает и выполняет блокировку карты по её идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно заблокирована",
                    content = @Content(schema = @Schema(example = "Карта успешно заблокирована"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Карта уже заблокирована или невалидный запрос"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для выполнения операции"
            )
    })
    @PostMapping("/cards/block")
    public ResponseEntity<String> approveBlock(
            @Parameter(description = "Запрос на блокировку карты", required = true)
            @Valid @RequestBody BlockCardRequest request) {
        cardService.approveBlock(request.getCardId());
        return ResponseEntity.ok("Карта успешно заблокирована");
    }
    @Operation(
            summary = "Разблокировка карты",
            description = "Администратор разблокирует ранее заблокированную карту по её идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Карта успешно разблокирована",
                    content = @Content(schema = @Schema(example = "Карта успешно разблокирована"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Карта не заблокирована или невалидный запрос"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для выполнения операции"
            )
    })
    @PostMapping("/cards/unblock")
    public ResponseEntity<String> unblock(
            @Parameter(description = "Запрос на разблокировку карты", required = true)
            @Valid @RequestBody BlockCardRequest request) {
        cardService.unblock(request.getCardId());
        return ResponseEntity.ok("Карта успешно разблокирована");
    }

    @Operation(
            summary = "Получить все транзакции",
            description = "Возвращает историю всех транзакций в системе с поддержкой пагинации. Доступно только администратору."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список транзакций успешно получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для доступа к ресурсу"
            )
    })
    @GetMapping("/transaction")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(
            @Parameter(
                    description = "Параметры пагинации и сортировки",
                    example = "{\n  \"page\": 0,\n  \"size\": 50,\n  \"sort\": \"transactionDate,desc\"\n}"
            )
            Pageable pageable) {
        List<TransactionDto> transactionDtos = transactionService.getAllTransaction(pageable).getContent();
        return ResponseEntity.ok(transactionDtos);
    }


    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("Пользователь успешно удалён");
    }
}