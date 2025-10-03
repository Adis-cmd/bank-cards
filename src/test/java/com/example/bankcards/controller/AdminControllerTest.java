package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private AuthDto authDto;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Даниэль")
                .surname("Петров")
                .email("test@gmail.com")
                .build();

        authDto = AuthDto.builder()
                .name("Даниэль")
                .surname("Петров")
                .email("test@gmail.com")
                .password("password123")
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_shouldReturnUsers() throws Exception {
        Page<UserDto> page = new PageImpl<>(List.of(userDto));
        when(userService.getAllUser(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Даниэль"))
                .andExpect(jsonPath("$[0].surname").value("Петров"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"));

        verify(userService).getAllUser(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_withEmptyList_shouldReturnEmptyArray() throws Exception {
        Page<UserDto> page = new PageImpl<>(List.of());
        when(userService.getAllUser(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAllUser(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Пользователь успешно создан"));

        verify(userService).register(any(AuthDto.class));
    }

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        authDto.setEmail("invalid-email");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any(AuthDto.class));
    }

    @Test
    void createUser_withBlankName_shouldReturnBadRequest() throws Exception {
        authDto.setName("");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any(AuthDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void approveBlock_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/admin/cards/block")
                        .param("cardId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта успешно заблокирована"));

        verify(cardService).approveBlock(1L);
    }

    @Test
    void approveBlock_withoutCardId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/admin/cards/block"))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).approveBlock(anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unblock_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/admin/cards/unblock")
                        .param("cardId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта успешно разблокирована"));

        verify(cardService).unblock(1L);
    }

    @Test
    void unblock_withoutCardId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/admin/cards/unblock"))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).unblock(anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTransactions_shouldReturnTransactions() throws Exception {
        Page<TransactionDto> page = new PageImpl<>(List.of(transactionDto));
        when(transactionService.getAllTransaction(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].fromCardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$[0].toCardNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].amount").value(250.75));

        verify(transactionService).getAllTransaction(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTransactions_withEmptyList_shouldReturnEmptyArray() throws Exception {
        Page<TransactionDto> page = new PageImpl<>(List.of());
        when(transactionService.getAllTransaction(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(transactionService).getAllTransaction(any(Pageable.class));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_shouldReturnOk_whenUserExists() throws Exception {
        doNothing().when(userService).deleteUser("test@gmail.com");

        mockMvc.perform(delete("/api/admin/users/{email}", "test@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь успешно удалён"));

        verify(userService).deleteUser("test@gmail.com");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        doThrow(new com.example.bankcards.exception.UserNotFoundException())
                .when(userService).deleteUser("nonexistent@gmail.com");

        mockMvc.perform(delete("/api/admin/users/{email}", "nonexistent@gmail.com"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser("nonexistent@gmail.com");
    }

}