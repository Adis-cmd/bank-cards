package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardMaskUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;
    private final UserService userService;



    @Transactional
    @Override
    public void createDepositTransaction(Card card, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .fromCard(card)
                .toCard(card)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        repository.save(transaction);
    }


    @Transactional
    @Override
    public void createTransferTransaction(Card fromCard, Card toCard, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

        repository.save(transaction);
    }


    @Override
    public Page<TransactionDto> getAllTransactionUser(String email, Pageable pageable) {
        User user = userService.findUserByEmail(email);
        Page<Transaction> transactions = repository.findAllTransactionUser(user.getId(), pageable);

        return transactions.map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setFromCardNumber(CardMaskUtil.mask(transaction.getFromCard().getCardNumber()));
            dto.setToCardNumber(CardMaskUtil.mask(transaction.getToCard().getCardNumber()));
            dto.setAmount(transaction.getAmount());
            dto.setDateTime(transaction.getTransactionDate());
            dto.setStatus(transaction.getStatus());
            return dto;
        });
    }

    @Override
    public Page<TransactionDto> getAllTransaction(Pageable pageable) {
        Page<Transaction> transactions = repository.findAllTransaction(pageable);

        return transactions.map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setFromCardNumber(CardMaskUtil.mask(transaction.getFromCard().getCardNumber()));
            dto.setToCardNumber(CardMaskUtil.mask(transaction.getToCard().getCardNumber()));
            dto.setAmount(transaction.getAmount());
            dto.setDateTime(transaction.getTransactionDate());
            dto.setStatus(transaction.getStatus());
            return dto;
        });
    }
}
