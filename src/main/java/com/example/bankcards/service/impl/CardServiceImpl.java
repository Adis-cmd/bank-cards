package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository repository;
    private final UserService userService;


    @Override
    @Transactional
    public CardDto createCard(String email) {
        User user = userService.findUserByEmail(email);

        Card newCard = Card.builder()
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .expirationDate(LocalDateTime.now().plusYears(1))
                .cardNumber(generateUniqueCardNumber())
                .build();

        repository.save(newCard);

        return new CardDto();
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
