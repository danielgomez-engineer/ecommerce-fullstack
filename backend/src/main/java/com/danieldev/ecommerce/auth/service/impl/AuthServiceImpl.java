package com.danieldev.ecommerce.auth.service.impl;

import com.danieldev.ecommerce.auth.dto.AuthResponse;
import com.danieldev.ecommerce.auth.dto.LoginRequest;
import com.danieldev.ecommerce.auth.dto.RegisterRequest;
import com.danieldev.ecommerce.auth.dto.UserResponse;
import com.danieldev.ecommerce.auth.service.AuthService;
import com.danieldev.ecommerce.security.JwtService;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.shared.exception.ResourceNotFoundException;
import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.entity.Role;
import com.danieldev.ecommerce.user.entity.User;
import com.danieldev.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException("Email is already registered", HttpStatus.CONFLICT);
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName().trim());
        customer.setEmail(normalizedEmail);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setRole(Role.CUSTOMER);
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer saved = userRepository.save(customer);
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        return buildAuthResponse(user);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole());

        if (user instanceof Customer customer) {
            builder.phone(customer.getPhone()).address(customer.getAddress());
        }
        return builder.build();
    }
}