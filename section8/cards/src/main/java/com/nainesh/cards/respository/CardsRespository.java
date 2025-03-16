package com.nainesh.cards.respository;

import com.nainesh.cards.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardsRespository extends JpaRepository<Cards, Long> {

    Optional<Cards> findByMobileNumber(String mobileNumber);
    Optional<Cards> findByCardNumber(String cardNumber);
}


