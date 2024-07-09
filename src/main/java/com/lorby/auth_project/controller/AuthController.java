package com.lorby.auth_project.controller;

import com.lorby.auth_project.dto.*;
import com.lorby.auth_project.entity.User;
import com.lorby.auth_project.exception.NotFoundException;
import com.lorby.auth_project.service.AuthService;
import com.lorby.auth_project.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Validated
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(
            summary = "Register a new user",
            description = "Register a new user with email, username, and password. Send a confirmation link.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Register request DTO containing email, username, password, and password confirmation.",
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
        authService.register(registerRequestDto);
        return new ResponseEntity<>("Confirmation link sent to your email", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Confirm email",
            description = "Confirm a user's email using a token sent to his/her email address.",
            parameters = @Parameter(
                    name = "token",
                    description = "Token sent to the user's email for confirmation",
                    required = true,
                    schema = @Schema(type = "string")
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
                    @ApiResponse(responseCode = "502", description = "Link is expired", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Invalid token", content = @Content),
            }
    )
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        try {
            authService.confirmEmail(token);
            return new ResponseEntity<>("Email confirmed successfully", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Resend confirmation email",
            description = "Resend the confirmation email to the user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Confirmation request DTO containing the user's email and username",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfirmationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Confirmation link resent to your email"),
                    @ApiResponse(responseCode = "400", description = "Invalid email", content = @Content),
                    @ApiResponse(responseCode = "409", description = "User is already confirmed", content = @Content),
                    @ApiResponse(responseCode = "502", description = "Failed to send email", content = @Content)
            }
    )
    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resend(@RequestBody ConfirmationRequestDto confirmationRequestDto){
        authService.sendConfirmationEmail(confirmationRequestDto);
        return new ResponseEntity<>("Confirmation link resent to your email", HttpStatus.OK);
    }

    @Operation(
            summary = "User login",
            description = "Authenticate a user and return access and refresh tokens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login request DTO containing username and password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid username or password", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Account has not been enabled. Confirm your email", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto){
        LoginResponseDto responseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Refresh the access token using a valid refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh request DTO containing the refresh token",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Refresh token is expired or invalid", content = @Content)
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody RefreshRequestDto refreshRequestDto){
        LoginResponseDto response = authService.refreshToken(refreshRequestDto.refreshToken());
        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Invalidate refresh token",
            description = "Accepts refresh token string for invalidation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logged out successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid token")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken, HttpServletRequest request) {
        authService.logout(refreshToken, request);
        return ResponseEntity.ok("Logged out successfully");
    }

}
