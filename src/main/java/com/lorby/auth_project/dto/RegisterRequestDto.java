package com.lorby.auth_project.dto;

import com.lorby.auth_project.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record RegisterRequestDto(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email must contain '@' and '.'")
        String email,
        @NotBlank(message = "Username is mandatory")
        @Size(min = 3, max = 20, message = "Username length should be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Username can only contain Latin characters")
        String username,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 15, message = "Password should be between 8 and 15 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character")
        String password,
        @NotBlank(message = "Password confirmation is mandatory")
        String confirmPassword
) {
}
