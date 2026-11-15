package com.danieldev.ecommerce.auth.controller;

import com.danieldev.ecommerce.auth.dto.AuthResponse;
import com.danieldev.ecommerce.auth.dto.LoginRequest;
import com.danieldev.ecommerce.auth.dto.RegisterRequest;
import com.danieldev.ecommerce.auth.dto.UserResponse;
import com.danieldev.ecommerce.auth.service.AuthService;
import com.danieldev.ecommerce.security.CustomUserPrincipal;
import com.danieldev.ecommerce.shared.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login and profile endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account")
    public ResponseEntity<GeneralResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT")
    public ResponseEntity<GeneralResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(GeneralResponse.success(response, "Login successful"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user details")
    public ResponseEntity<GeneralResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserPrincipal principal) {
        UserResponse response = authService.getCurrentUser(principal.email());
        return ResponseEntity.ok(GeneralResponse.success(response, "User profile retrieved successfully"));
    }
}