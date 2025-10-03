package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        cardDto = CardDto.builder()
                .balance(BigDecimal.valueOf(1000.00))
                .name("Даниэль")
                .surname("Петров")
                .cardNumber("**** **** **** 4312")
                .expirationDate(LocalDateTime.now().plusYears(2))
                .status(CardStatus.ACTIVE)
                .build();
    }

    @Test
    @WithMockUser(username = "test")
    void viewBalance_shouldReturnBalance() throws Exception {
        when(cardService.viewBalance(1L, "test")).thenReturn(BigDecimal.valueOf(1000.00));

        mockMvc.perform(get("/api/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1000.00));

        verify(cardService).viewBalance(1L, "test");
    }

    @Test
    @WithMockUser(username = "test")
    void viewBalance_shouldReturnBalanceWithCorrectScale() throws Exception {
        when(cardService.viewBalance(1L, "test")).thenReturn(BigDecimal.valueOf(1000.567));

        mockMvc.perform(get("/api/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.57));

        verify(cardService).viewBalance(1L, "test");
    }

    @Test
    void viewBalance_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/cards/1/balance"))
                .andExpect(status().isForbidden());

        verify(cardService, never()).viewBalance(anyLong(), anyString());
    }

    @Test
    @WithMockUser(username = "test")
    void getCardById_shouldReturnCard() throws Exception {
        when(cardService.getCardById(1L, "test")).thenReturn(cardDto);

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.name").value("Даниэль"))
                .andExpect(jsonPath("$.surname").value("Петров"))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 4312"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(cardService).getCardById(1L, "test");
    }

    @Test
    void getCardById_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isForbidden());

        verify(cardService, never()).getCardById(anyLong(), anyString());
    }
}