package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    private Card testCard1;
    private Card testCard2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCard1 = Card.builder()
                .id(1L)
                .cardNumber("1234567812345678")
                .balance(BigDecimal.valueOf(1000))
                .build();

        testCard2 = Card.builder()
                .id(2L)
                .cardNumber("8765432187654321")
                .balance(BigDecimal.valueOf(500))
                .build();
    }

    @Test
    void createDepositTransaction_ShouldSaveTransaction() {
        transactionService.createDepositTransaction(testCard1, BigDecimal.valueOf(200));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction savedTransaction = captor.getValue();
        assert savedTransaction.getFromCard() == testCard1;
        assert savedTransaction.getToCard() == testCard1;
        assert savedTransaction.getAmount().equals(BigDecimal.valueOf(200));
        assert savedTransaction.getStatus() == TransactionStatus.SUCCESS;
    }

    @Test
    void createTransferTransaction_ShouldSaveTransaction() {
        transactionService.createTransferTransaction(testCard1, testCard2, BigDecimal.valueOf(300));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction savedTransaction = captor.getValue();
        assert savedTransaction.getFromCard() == testCard1;
        assert savedTransaction.getToCard() == testCard2;
        assert savedTransaction.getAmount().equals(BigDecimal.valueOf(300));
        assert savedTransaction.getStatus() == TransactionStatus.SUCCESS;
    }
}
