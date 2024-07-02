package com.lorby.auth_project.validation.validator;

import com.lorby.auth_project.dto.RegisterRequestDto;
import com.lorby.auth_project.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequestDto> {

    @Override
    public boolean isValid(RegisterRequestDto registerRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        return registerRequestDto.password().equals(registerRequestDto.confirmPassword());
    }
}
