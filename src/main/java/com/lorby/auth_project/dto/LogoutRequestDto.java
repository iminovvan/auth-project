package com.lorby.auth_project.dto;

import lombok.Data;

@Data
public record LogoutRequestDto(
        String refreshToken
) {
}
