package com.example.bankcards.controller;

import com.example.bankcards.dto.DepositRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepositRequest depositRequest;
    private TransferRequestDto transferRequest;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        depositRequest = DepositRequest.builder()
                .cardNumber("1234567812345678")
                .amount(BigDecimal.valueOf(500.00))
                .build();

        transferRequest = TransferRequestDto.builder()
                .fromCardNumber("1234567812345678")
                .toCardNumber("8765432187654321")
                .amount(BigDecimal.valueOf(1000.50))
                .build();

        transactionDto = TransactionDto.builder()
                .fromCardNumber("**** **** **** 1234")
                .toCardNumber("**** **** **** 5678")
                .dateTime(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .amount(BigDecimal.valueOf(250.75))
                .build();
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void deposit_shouldReturnOk() throws Exception {
        doNothing().when(cardService).depositBalance(anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс успешно пополнен"));

        verify(cardService).depositBalance("1234567812345678", BigDecimal.valueOf(500.00));
    }

    @Test
    void deposit_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        depositRequest.setAmount(BigDecimal.valueOf(-100));

        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).depositBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    void deposit_withBlankCardNumber_shouldReturnBadRequest() throws Exception {
        depositRequest.setCardNumber("");

        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).depositBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void transfer_shouldReturnOk() throws Exception {
        doNothing().when(cardService).transferBalance(any(TransferRequestDto.class));

        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод выполнен успешно"));

        verify(cardService).transferBalance(any(TransferRequestDto.class));
    }

    @Test
    void transfer_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        transferRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).transferBalance(any(TransferRequestDto.class));
    }

    @Test
    void transfer_withBlankFromCardNumber_shouldReturnBadRequest() throws Exception {
        transferRequest.setFromCardNumber("");

        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).transferBalance(any(TransferRequestDto.class));
    }

    @Test
    void transfer_withBlankToCardNumber_shouldReturnBadRequest() throws Exception {
        transferRequest.setToCardNumber("");

        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).transferBalance(any(TransferRequestDto.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void getMyTransaction_shouldReturnTransactions() throws Exception {
        Page<TransactionDto> page = new PageImpl<>(List.of(transactionDto));
        when(transactionService.getAllTransactionUser(eq("test"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/transaction/my")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].fromCardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$[0].toCardNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].amount").value(250.75));

        verify(transactionService).getAllTransactionUser(eq("test"), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void getMyTransaction_withEmptyList_shouldReturnEmptyArray() throws Exception {
        Page<TransactionDto> page = new PageImpl<>(List.of());
        when(transactionService.getAllTransactionUser(eq("test"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/transaction/my")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(transactionService).getAllTransactionUser(eq("test"), any(Pageable.class));
    }
}