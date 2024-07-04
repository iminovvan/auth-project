package com.lorby.auth_project.service;

import com.lorby.auth_project.entity.Token;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.entity.enums.TokenType;
import com.lorby.auth_project.exception.NotFoundException;
import com.lorby.auth_project.exception.TokenExpiredException;
import com.lorby.auth_project.repository.TokenRepository;
import com.lorby.auth_project.repository.UserRepository;
import com.lorby.auth_project.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final long BASE_EXPIRATION_MINUTES = 5;
    private static final long EMAIL_VERIFICATION_EXPIRATION = BASE_EXPIRATION_MINUTES * 3; // 15 minutes
    private static final long ACCESS_TOKEN_EXPIRATION = BASE_EXPIRATION_MINUTES * 3; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = BASE_EXPIRATION_MINUTES * 288; // 24 hours

    public Token generateToken(User user, TokenType tokenType){
        long expirationMinutes;
        switch (tokenType) {
            case EMAIL_CONFIRMATION:
                expirationMinutes = EMAIL_VERIFICATION_EXPIRATION;
                break;
            case REFRESH_TOKEN:
                expirationMinutes = REFRESH_TOKEN_EXPIRATION;
                break;
            case ACCESS_TOKEN:
                expirationMinutes = ACCESS_TOKEN_EXPIRATION;
                break;
            default:
                expirationMinutes = ACCESS_TOKEN_EXPIRATION;
                break;
        }

        String tokenValue = (tokenType == TokenType.ACCESS_TOKEN || tokenType == TokenType.REFRESH_TOKEN)
                ? jwtService.generateToken(user, expirationMinutes)
                : UUID.randomUUID().toString();

        Token token = new Token();
        token.setUser(user);
        token.setToken(tokenValue);
        token.setTokenType(tokenType);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        return tokenRepository.save(token);
    }

    public Optional<Token> validateToken(String tokenValue) {
        Optional<Token> token = tokenRepository.findByToken(tokenValue);
        if (token.isPresent() && token.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token is expired");
        }
        return token;
    }

}
