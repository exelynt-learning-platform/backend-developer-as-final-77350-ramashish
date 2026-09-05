package com.example.Ramashish.booking.dto.auth;

public record AuthResponse(String token,
                           String username,
                           String role,
                           long expiresInMs) {
}
