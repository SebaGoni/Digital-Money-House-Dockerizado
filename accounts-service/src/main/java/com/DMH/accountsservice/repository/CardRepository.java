package com.DMH.accountsservice.repository;

import com.DMH.accountsservice.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long accountId); // Obtener tarjetas por ID de cuenta
    Optional<Card> findByNumber(String number);
}
