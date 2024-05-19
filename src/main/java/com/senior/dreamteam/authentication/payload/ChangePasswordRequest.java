package com.senior.dreamteam.authentication.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record ChangePasswordRequest(
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,
        @NotEmpty(message = "Password cannot be empty")
        String password,
        @NotEmpty(message = "New password cannot be empty") @Length(min = 6, message = "Password length must more than 6") @Length(max = 20, message = "Password length must less than 20")
        String newPassword
) {
}