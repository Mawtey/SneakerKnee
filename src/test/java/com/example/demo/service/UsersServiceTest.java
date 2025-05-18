package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    @Test
    void registerUser_Success() {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(usersRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(usersRepository.save(any())).thenReturn(user);

        Users result = usersService.registerUser(user);

        assertNotNull(result);
        verify(usersRepository).save(any());
    }

    @Test
    void registerUser_EmailExists_ThrowsException() {
        Users user = new Users();
        user.setEmail("test@example.com");

        when(usersRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usersService.registerUser(user));
    }
}