package com.senior.dreamteam.authentication.payload;

public record LoginRequest (
        String email,
        String password
) { }