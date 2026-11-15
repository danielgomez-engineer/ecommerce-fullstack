package com.danieldev.ecommerce.auth.service;

import com.danieldev.ecommerce.auth.dto.AuthResponse;
import com.danieldev.ecommerce.auth.dto.LoginRequest;
import com.danieldev.ecommerce.auth.dto.RegisterRequest;
import com.danieldev.ecommerce.auth.dto.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String email);
}