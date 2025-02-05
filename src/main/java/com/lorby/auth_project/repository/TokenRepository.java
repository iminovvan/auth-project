package com.lorby.auth_project.repository;

import com.lorby.auth_project.entity.Token;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.entity.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String tokenValue);
    void deleteByToken(String token);
}
