package com.senior.dreamteam.authentication.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record RegisterRequest(
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotEmpty(message = "Name cannot be empty")
        String name,
        @NotEmpty(message = "Password cannot be empty")
        String password
) {
}