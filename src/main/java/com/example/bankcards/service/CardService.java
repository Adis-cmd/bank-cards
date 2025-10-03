package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {
    void createCard(String email);

    Page<CardDto> getUserCards(Pageable pageable, String email);

    void depositBalance(String cardNumber, BigDecimal amount);

    @Transactional
    void transferBalance(TransferRequestDto dto);

    CardDto getCardById(Long id, String email);

    BigDecimal viewBalance(Long id, String email);

    void requestBlock(String email, Long id);

    void approveBlock(Long id);

    void unblock(Long id);
}
