package com.lorby.auth_project.dto;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
