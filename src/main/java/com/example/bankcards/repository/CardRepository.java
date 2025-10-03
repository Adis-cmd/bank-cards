package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String cardNumber);

    @Query(nativeQuery = true, value = "select * from cards c where c.owner_id = :userId")
    Page<Card> getAllCardForUser(@Param("userId") Long userId, Pageable pageable);


    @Query(nativeQuery = true, value = "select * from cards c where c.card_number = :cardNumber")
    Optional<Card> findCardByCardNumber(@Param("cardNumber") String cardNumber);
}
