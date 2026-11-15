package com.danieldev.ecommerce.security;

import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.entity.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void generateAndValidateToken_extractsExpectedClaims() {
        Customer user = new Customer();
        user.setId(7L);
        user.setEmail("jane@demo.com");
        user.setRole(Role.CUSTOMER);

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token)).isTrue();
        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo("jane@demo.com");
        assertThat(claims.get("userId", Long.class)).isEqualTo(7L);
        assertThat(claims.get("role", String.class)).isEqualTo("CUSTOMER");
    }

    @Test
    void isTokenValid_whenTokenIsTampered_returnsFalse() {
        assertThat(jwtService.isTokenValid("invalid.jwt.token")).isFalse();
    }
}