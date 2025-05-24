package com.example.demo.controller;

import com.example.demo.security.AuthRequest;
import com.example.demo.security.AuthResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_ShouldReturnToken() {
        // Arrange
        AuthRequest request = new AuthRequest("test@example.com", "password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any())).thenReturn("test.token");

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test.token", response.getBody().getToken());
    }
}