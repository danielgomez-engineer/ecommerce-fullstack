package com.danieldev.ecommerce.auth.service;

import com.danieldev.ecommerce.auth.dto.AuthResponse;
import com.danieldev.ecommerce.auth.dto.LoginRequest;
import com.danieldev.ecommerce.auth.dto.RegisterRequest;
import com.danieldev.ecommerce.auth.service.impl.AuthServiceImpl;
import com.danieldev.ecommerce.security.JwtService;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.entity.Role;
import com.danieldev.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthServiceImpl authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService);

    @Test
    void register_withNewEmail_savesCustomerWithEncodedPasswordAndReturnsToken() {
        RegisterRequest req = RegisterRequest.builder()
                .fullName("Jane Doe")
                .email("JANE@demo.com")
                .password("secret123")
                .build();

        when(userRepository.existsByEmail("jane@demo.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$encoded");
        when(userRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        AuthResponse response = authService.register(req);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("jane@demo.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("$2a$encoded");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
    }

    @Test
    void register_whenEmailAlreadyExists_throws409Conflict() {
        RegisterRequest req = RegisterRequest.builder()
                .fullName("Jane")
                .email("taken@demo.com")
                .password("secret123")
                .build();

        when(userRepository.existsByEmail("taken@demo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BusinessException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_whenPasswordDoesNotMatch_throws401Unauthorized() {
        Customer user = new Customer();
        user.setEmail("jane@demo.com");
        user.setPassword("$2a$encoded");

        when(userRepository.findByEmail("jane@demo.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        LoginRequest req = new LoginRequest("jane@demo.com", "wrong");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BusinessException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}