package com.lorby.auth_project.controller;

import com.lorby.auth_project.dto.*;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.exception.NotFoundException;
import com.lorby.auth_project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication, authorization, and confirmation"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@Validated
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Register a new user with email, username, and password. Send a confirmation link.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto registerRequestDto){
        log.info("Register request received: {}", registerRequestDto);
        authService.register(registerRequestDto);
        return new ResponseEntity<>("Confirmation link sent to your email", HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        log.info("Email confirmation request received with token: {}", token);
        try {
            authService.confirmEmail(token);
            return new ResponseEntity<>("Email confirmed successfully", HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("Error confirming email: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resend(@RequestBody ConfirmationRequestDto confirmationRequestDto){
        authService.sendConfirmationEmail(confirmationRequestDto);
        return new ResponseEntity<>("Confirmation link resent to your email", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto){
        LoginResponseDto responseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody RefreshRequestDto refreshRequestDto){
        LoginResponseDto response = authService.refreshToken(refreshRequestDto.refreshToken());
        return ResponseEntity.ok(response);
    }

}
