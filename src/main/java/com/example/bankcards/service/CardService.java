package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface CardService {
    CardDto createCard(String email);

    Page<CardDto> getUserCards(Pageable pageable, String email);
}
