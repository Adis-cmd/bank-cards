package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardMaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@example.com")
                .build();

        card = Card.builder()
                .id(1L)
                .owner(user)
                .cardNumber("1234567812345678")
                .balance(new BigDecimal("1000.00"))
                .status(CardStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusYears(1))
                .build();
    }

    @Test
    void getCardById_ShouldReturnDto() {
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        CardDto cardDto = cardService.getCardById(card.getId(), user.getEmail());

        assertNotNull(cardDto);
        assertEquals(user.getName(), cardDto.getName());
        assertEquals(user.getSurname(), cardDto.getSurname());
        assertEquals(card.getBalance(), cardDto.getBalance());
        assertEquals(card.getStatus(), cardDto.getStatus());
        assertEquals(card.getExpirationDate(), cardDto.getExpirationDate());

        String expectedMasked = CardMaskUtil.mask(card.getCardNumber());
        assertEquals(expectedMasked, cardDto.getCardNumber());

        verify(userService, times(1)).findUserByEmail(user.getEmail());
        verify(cardRepository, times(1)).findById(card.getId());
    }

    @Test
    void getUserCards_ShouldReturnPageOfDtosWithMaskedCardNumbers() {
        // Мокаем сервис и репозиторий
        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        Page<Card> cardsPage = new PageImpl<>(List.of(card));
        when(cardRepository.getAllCardForUser(user.getId(), Pageable.unpaged())).thenReturn(cardsPage);

        Page<CardDto> result = cardService.getUserCards(Pageable.unpaged(), user.getEmail());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        CardDto dto = result.getContent().get(0);
        assertEquals(card.getCardNumber(), dto.getCardNumber());
        assertEquals(card.getBalance(), dto.getBalance());
        assertEquals(card.getStatus(), dto.getStatus());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
    }
}
