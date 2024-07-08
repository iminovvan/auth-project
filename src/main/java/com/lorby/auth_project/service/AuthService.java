package com.lorby.auth_project.service;

import com.lorby.auth_project.dto.ConfirmationRequestDto;
import com.lorby.auth_project.dto.LoginRequestDto;
import com.lorby.auth_project.dto.LoginResponseDto;
import com.lorby.auth_project.dto.RegisterRequestDto;
import com.lorby.auth_project.entity.Role;
import com.lorby.auth_project.entity.Token;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.entity.enums.TokenType;
import com.lorby.auth_project.exception.*;
import com.lorby.auth_project.repository.RoleRepository;
import com.lorby.auth_project.repository.TokenRepository;
import com.lorby.auth_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;
    public void register(RegisterRequestDto registerRequestDto) {
        log.info("Registering user: {}", registerRequestDto.username());
        if(userRepository.findByUsername(registerRequestDto.username()).isPresent()){
            throw new UsernameExistsException("Username already exists");
        }
        if(userRepository.findByEmail(registerRequestDto.email()).isPresent()){
            throw new EmailExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(registerRequestDto.email());
        user.setUsername(registerRequestDto.username());
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));
        user.setIsConfirmed(false);
        Role userRole = roleRepository.findByName("USER").
                orElseThrow(() -> new NotFoundException("Role not found."));
        user.addRole(userRole);
        userRepository.save(user);
        log.info("User {} saved successfully", registerRequestDto.username());
        ConfirmationRequestDto confirmDto = new ConfirmationRequestDto(user.getUsername(), user.getEmail());
        sendConfirmationEmail(confirmDto);
    }

    public void sendConfirmationEmail(ConfirmationRequestDto confirmationRequestDto){
        Optional<User> user = userRepository.findByUsername(confirmationRequestDto.username());
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }
        if(user.get().getIsConfirmed()){
            throw new UsernameExistsException("User is already confirmed");
        }
        Token token = tokenService.generateToken(user.get(), TokenType.EMAIL_CONFIRMATION);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        /*
        emailService.sendEmail(user.get().getEmail(), "Email Confirmation",
                "Please click on the following link to confirm your email: <a href=\"" + confirmationLink + "\">Verify Email<a>");
                String confirmationLink;
        try {
            confirmationLink = baseUrl + "?token=" + URLEncoder.encode(token.getToken(), StandardCharsets.UTF_8.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL", e);
        }
         */
        String confirmationLink;
        try {
            confirmationLink = "http://auth-project-production-d0e6.up.railway.app/api/auth/confirm?token=" + URLEncoder.encode(token.getToken(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL", e);
        }
        String content = "Please click on the following link to confirm your email: " + confirmationLink;
        emailService.sendEmail(user.get().getEmail(), "Email Confirmation", content);
    }

    public void confirmEmail(String tokenValue) {
        try{
            Optional<Token> confirmationToken = tokenService.validateToken(tokenValue);
            if (confirmationToken.isPresent()) {
                User user = confirmationToken.get().getUser();
                user.setIsConfirmed(true);
                userRepository.save(user);
            } else {
                throw new NotFoundException("Invalid token");
            }
        } catch (TokenExpiredException ex){
            throw new TokenExpiredException("Link is expired");
        }
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password())
            );
            User user =(User) authentication.getPrincipal();
            if(!user.getIsConfirmed()){
                throw new AccountNotConfirmedException("Account has not been enabled. Confirm your email");
            }
            String accessToken = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);
            return new LoginResponseDto(accessToken, refreshToken);
        } catch(AuthenticationException ex){
            if(ex instanceof DisabledException){
                throw new AccountNotConfirmedException("Account has not been enabled. Confirm your email");
            } else {
                throw new InvalidCredentialsException("Invalid username or password");
            }
        }
    }

    private String generateAccessToken(User user){
        Token accessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN);
        return accessToken.getToken();
    }

    private String generateRefreshToken(User user){
        Token refreshToken = tokenService.generateToken(user, TokenType.REFRESH_TOKEN);
        return refreshToken.getToken();
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        Optional<Token> refreshTokenEntity = tokenService.validateToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            throw new TokenExpiredException("Refresh token is expired or invalid");
        }

        User user = refreshTokenEntity.get().getUser();
        String newAccessToken = tokenService.generateToken(user, TokenType.ACCESS_TOKEN).getToken();
        String newRefreshToken = tokenService.generateToken(user, TokenType.REFRESH_TOKEN).getToken();

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

}
