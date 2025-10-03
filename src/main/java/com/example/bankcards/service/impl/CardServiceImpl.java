package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository repository;
    private final UserService userService;
    private final TransactionService transactionService;
    @Value("${app.security.max-transfer-amount:250000}")
    private  BigDecimal maxTransferAmount;

    @Value("${card-expiration-years:2}")
    private  Integer cardExperienceDay;


    @Override
    @Transactional
    public void createCard(String email) {
        User user = userService.findUserByEmail(email);

        Card newCard = Card.builder()
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .expirationDate(LocalDateTime.now().plusYears(cardExperienceDay))
                .cardNumber(generateUniqueCardNumber())
                .build();

        repository.save(newCard);
    }


    @Override
    public Page<CardDto> getUserCards(Pageable pageable, String email) {
        User user = userService.findUserByEmail(email);
        Page<Card> allCard = repository.getAllCardForUser(user.getId(), pageable);

        return allCard.map(card -> {
            CardDto dto = CardDto.builder()
                    .balance(card.getBalance())
                    .name(card.getOwner().getName())
                    .expirationDate(card.getExpirationDate())
                    .surname(card.getOwner().getSurname())
                    .cardNumber(card.getCardNumber())
                    .status(card.getStatus())
                    .build();
            return dto;
        });
    }


    @Override
    public void depositBalance(String cardNumber, BigDecimal amount) {
        Card card = repository.findCardByCardNumber(cardNumber).orElseThrow(CardNotFoundException::new);
        card.setBalance(card.getBalance().add(amount));
        repository.save(card);
        transactionService.createDepositTransaction(card, amount);
    }


    @Transactional
    @Override
    public void transferBalance(TransferRequestDto dto) {
        Card fromCard = repository.findCardByCardNumber(dto.getFromCardNumber()).orElseThrow(CardNotFoundException::new);
        Card toCard = repository.findCardByCardNumber(dto.getToCardNumber()).orElseThrow(CardNotFoundException::new);


        if (dto.getAmount().compareTo(maxTransferAmount) > 0) {
            throw new CardOperationException(
                    String.format("Превышена максимальная сумма перевода. Максимум: %s руб.", maxTransferAmount)
            );
        }

        if (fromCard.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на карте");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(dto.getAmount()));
        repository.save(fromCard);

        toCard.setBalance(toCard.getBalance().add(dto.getAmount()));
        repository.save(toCard);

        transactionService.createTransferTransaction(fromCard, toCard, dto.getAmount());
    }


    @Override
    public CardDto getCardById(Long id, String email) {
        User user = userService.findUserByEmail(email);
        Card card = repository.findById(id)
                .filter(c -> c.getOwner().getEmail().equals(user.getEmail()))
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена или нет доступа"));

        return CardDto.builder()
                .cardNumber(CardMaskUtil.mask(card.getCardNumber()))
                .balance(card.getBalance())
                .surname(card.getOwner().getSurname())
                .name(card.getOwner().getName())
                .status(card.getStatus())
                .expirationDate(card.getExpirationDate())
                .build();
    }


    @Override
    public BigDecimal viewBalance(Long id, String email) {
        User user = userService.findUserByEmail(email);
        Card card = repository.findById(id)
                .filter(c -> c.getOwner().getEmail().equals(user.getEmail()))
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена или нет доступа"));
        return card.getBalance();
    }

    @Override
    public void requestBlock(String email, Long id) {
        User user = userService.findUserByEmail(email);
        Card card = repository.findById(id)
                .filter(c -> c.getOwner().getEmail().equals(user.getEmail()))
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена или нет доступа"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException("Карта уже заблокирована");
        }
        if (card.getStatus() == CardStatus.BLOCK_REQUESTED) {
            throw new CardOperationException("Запрос на блокировку уже отправлен");
        }

        card.setStatus(CardStatus.BLOCK_REQUESTED);
        repository.save(card);
    }

    @Override
    public void approveBlock(Long id) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException("Карта уже заблокированна");
        }

        card.setStatus(CardStatus.BLOCKED);
        repository.save(card);
    }

    @Override
    public void unblock(Long id) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new CardOperationException("Карта уже активна");
        }
        if (card.getStatus() == CardStatus.BLOCK_REQUESTED) {
            throw new CardOperationException("Нельзя разблокировать карту, пока она в ожидании блокировки");
        }
        card.setStatus(CardStatus.ACTIVE);
        repository.save(card);
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = generateRandom16Digits();
        } while (repository.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    private String generateRandom16Digits() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
