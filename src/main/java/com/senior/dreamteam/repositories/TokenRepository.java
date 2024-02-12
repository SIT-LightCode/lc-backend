package com.senior.dreamteam.repositories;

import com.senior.dreamteam.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findTokenByToken(String token);
}
