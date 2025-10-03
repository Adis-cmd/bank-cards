package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(nativeQuery = true, value = "select * from transactions t where t.from_card_id = :userId")
    Page<Transaction> findAllTransactionUser(@Param("userId") Long userId, Pageable pageable);


    @Query(nativeQuery = true, value = "select * from transactions")
    Page<Transaction> findAllTransaction(Pageable pageable);
}
