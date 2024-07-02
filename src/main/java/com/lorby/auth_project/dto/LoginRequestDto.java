package com.lorby.auth_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "Username is mandatory")
        @Size(min = 3, max = 20, message = "Username length should be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Username can only contain Latin characters")
        String username,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 15, message = "Password should be between 8 and 15 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character")
        String password
) {
}
