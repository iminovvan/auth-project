package com.lorby.auth_project.dto;

import lombok.Data;

public record LogoutRequestDto(
        String refreshToken
) {
}
