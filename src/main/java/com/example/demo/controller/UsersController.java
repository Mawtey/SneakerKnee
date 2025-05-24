package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.security.AuthResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.UsersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    public UsersController(UsersService usersService, JwtService jwtService) {
        this.usersService = usersService;
        this.jwtService = jwtService;
        logger.info("UsersController initialized");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Users user) {
        logger.info("Attempt to register new user with email: {}", user.getEmail());
        try {
            Users registeredUser = usersService.registerUser(user);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());

            String jwtToken = jwtService.generateToken(registeredUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwtToken));
        } catch (IllegalArgumentException e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Integer id) {
        logger.debug("Fetching user by ID: {}", id);
        return usersService.getUserById(id)
                .map(user -> {
                    logger.debug("Found user with ID: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "This is admin only endpoint";
    }

    @GetMapping
    public List<Users> getAllUsers() {
        logger.debug("Fetching all users");
        List<Users> users = usersService.getAllUsers();
        logger.debug("Found {} users", users.size());
        return users;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Integer id, @Valid @RequestBody Users userDetails) {
        logger.info("Attempt to update user ID {} with data: {}", id, userDetails);
        try {
            Users updatedUser = usersService.updateUser(id, userDetails);
            logger.info("User ID {} updated successfully", id);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating user ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        logger.info("Attempt to delete user with ID: {}", id);
        try {
            usersService.deleteUser(id);
            logger.info("User ID {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting user ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}