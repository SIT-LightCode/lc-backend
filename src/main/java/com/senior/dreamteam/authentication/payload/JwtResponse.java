package com.senior.dreamteam.authentication.payload;

import jakarta.validation.constraints.NotEmpty;

public record JwtResponse(@NotEmpty(message = "Token cannot be empty") String token,
                          @NotEmpty(message = "Refresh-token cannot be empty") String refreshToken) {
}