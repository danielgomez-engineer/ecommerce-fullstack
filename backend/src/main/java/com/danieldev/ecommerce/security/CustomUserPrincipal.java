package com.danieldev.ecommerce.security;

public record CustomUserPrincipal(
        Long userId,
        String email,
        String role
) {
}