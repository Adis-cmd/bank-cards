package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {
    @Transactional
    void createDepositTransaction(Card card, BigDecimal amount);

    @Transactional
    void createTransferTransaction(Card fromCard, Card toCard, BigDecimal amount);

    Page<TransactionDto> getAllTransactionUser(String email, Pageable pageable);

    Page<TransactionDto> getAllTransaction(Pageable pageable);
}
