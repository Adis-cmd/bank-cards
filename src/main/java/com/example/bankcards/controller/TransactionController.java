package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Transaction Controller", description = "API для управления транзакциями и операциями с картами")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final CardService cardService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Пополнение баланса карты",
            description = "Позволяет пополнить баланс указанной карты на заданную сумму"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Баланс успешно пополнен",
                    content = @Content(schema = @Schema(example = "Баланс успешно пополнен"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Карта не найдена"
            )
    })
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @Parameter(description = "Данные для пополнения баланса", required = true)
            @Valid @RequestBody DepositRequest request) {
        cardService.depositBalance(request.getCardNumber(), request.getAmount());
        return ResponseEntity.ok("Баланс успешно пополнен");
    }

    @Operation(
            summary = "Перевод между картами",
            description = "Выполняет перевод денежных средств между двумя картами"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Перевод выполнен успешно",
                    content = @Content(schema = @Schema(example = "Перевод выполнен успешно"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса или недостаточно средств"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Одна из карт не найдена"
            )
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @Parameter(description = "Данные для перевода между картами", required = true)
            @Valid @RequestBody TransferRequestDto request) {
        cardService.transferBalance(request);
        return ResponseEntity.ok("Перевод выполнен успешно");
    }

    @Operation(
            summary = "Получить историю транзакций пользователя",
            description = "Возвращает историю всех транзакций текущего аутентифицированного пользователя с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "История транзакций успешно получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            )
    })
    @GetMapping("/my")
    public ResponseEntity<List<TransactionDto>> getMyTransaction(
            @Parameter(description = "Аутентифицированный пользователь", hidden = true)
            Principal principal,

            @Parameter(
                    description = "Параметры пагинации и сортировки",
                    example = "{\n  \"page\": 0,\n  \"size\": 10,\n  \"sort\": \"transactionDate,desc\"\n}"
            )
            Pageable pageable) {

        List<TransactionDto> transactionDtos = transactionService.getAllTransactionUser(principal.getName(), pageable).getContent();
        return ResponseEntity.ok(transactionDtos);
    }
}