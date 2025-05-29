package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.Users;
import com.example.demo.security.AuthResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsersController usersController;

    @Test
    void registerUser_Success() {
        // Arrange
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");
        Users registeredUser = new Users();
        registeredUser.setId(1);
        registeredUser.setEmail("test@example.com");

        when(usersService.registerUser(user)).thenReturn(registeredUser);
        when(jwtService.generateToken(registeredUser)).thenReturn("test-token");

        // Act
        ResponseEntity<?> response = usersController.registerUser(user);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        assertEquals("test-token", ((AuthResponse) response.getBody()).getToken());
    }

    @Test
    void registerUser_Failure() {
        // Arrange
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(usersService.registerUser(user))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act
        ResponseEntity<?> response = usersController.registerUser(user);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    void getUserById_Found() {
        // Arrange
        Integer userId = 1;
        Users user = new Users();
        user.setId(userId);
        user.setEmail("test@example.com");

        when(usersService.getUserById(userId))
                .thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Users> response = usersController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        Integer userId = 1;

        when(usersService.getUserById(userId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Users> response = usersController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminOnly_Success() {
        // Act
        String result = usersController.adminOnly();

        // Assert
        assertEquals("This is admin only endpoint", result);
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        Users user1 = new Users();
        user1.setId(1);
        Users user2 = new Users();
        user2.setId(2);

        when(usersService.getAllUsers())
                .thenReturn(Arrays.asList(user1, user2));

        // Act
        List<Users> users = usersController.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void updateUser_Success() {
        // Arrange
        Integer userId = 1;
        Users userDetails = new Users();
        userDetails.setName("Updated Name");
        Users updatedUser = new Users();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");

        when(usersService.updateUser(userId, userDetails))
                .thenReturn(updatedUser);

        // Act
        ResponseEntity<Users> response = usersController.updateUser(userId, userDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        Integer userId = 1;

        doNothing().when(usersService).deleteUser(userId);

        // Act
        ResponseEntity<Void> response = usersController.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usersService, times(1)).deleteUser(userId);
    }
}