package com.lorby.auth_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConfirmationRequestDto(
        @Size(min = 3, max = 20, message = "Username length should be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Username can only contain Latin characters")
        String username,
        @Email(message = "Email should be valid")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email must contain '@' and '.'")
        String email
) {
}
