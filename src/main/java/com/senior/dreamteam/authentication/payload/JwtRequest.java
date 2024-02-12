package com.senior.dreamteam.authentication.payload;

import jakarta.validation.constraints.NotEmpty;

public record JwtRequest(@NotEmpty(message = "Token cannot be empty") String token) { }