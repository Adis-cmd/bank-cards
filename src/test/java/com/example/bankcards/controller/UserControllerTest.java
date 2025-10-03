package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    @WithMockUser(username = "test", roles = "USER")
    void createCard_shouldReturnCreated() throws Exception {
        doNothing().when(cardService).createCard(anyString());

        mockMvc.perform(post("/api/user/create")
                        .principal(() -> "test"))
                .andExpect(status().isCreated());

        verify(cardService).createCard("test");
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void getUserCards_shouldReturnCards() throws Exception {
        Page<CardDto> page = new PageImpl<>(List.of(cardDto));
        when(cardService.getUserCards(any(Pageable.class), eq("test"))).thenReturn(page);

        mockMvc.perform(get("/api/user")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].balance").value(1000.00))
                .andExpect(jsonPath("$[0].name").value("Даниэль"))
                .andExpect(jsonPath("$[0].surname").value("Петров"))
                .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 4312"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(cardService).getUserCards(any(Pageable.class), eq("test"));
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void getUserCards_withEmptyList_shouldReturnEmptyArray() throws Exception {
        Page<CardDto> page = new PageImpl<>(List.of());
        when(cardService.getUserCards(any(Pageable.class), eq("test"))).thenReturn(page);

        mockMvc.perform(get("/api/user")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cardService).getUserCards(any(Pageable.class), eq("test"));
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void requestBlock_shouldReturnOk() throws Exception {
        doNothing().when(cardService).requestBlock(anyString(), anyLong());

        mockMvc.perform(post("/api/user/request-block")
                        .param("cardId", "1")
                        .principal(() -> "test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Запрос на блокировку успешно отправлен"));

        verify(cardService).requestBlock("test", 1L);
    }

    @Test
    @WithMockUser(username = "test", roles = "USER")
    void requestBlock_withoutCardId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/user/request-block")
                        .principal(() -> "test"))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).requestBlock(anyString(), anyLong());
    }
}