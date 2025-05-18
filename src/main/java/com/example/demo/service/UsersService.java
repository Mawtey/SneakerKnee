package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("UsersService initialized with PasswordEncoder");
    }

    @Transactional
    public Users registerUser(Users user) {
        logger.info("Attempting to register new user with email: {}", user.getEmail());
        try {
            if (usersRepository.existsByEmail(user.getEmail())) {
                logger.error("Registration failed - email already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            // Логируем факт хеширования пароля (без самого пароля)
            logger.debug("Hashing password for user: {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Users registeredUser = usersRepository.save(user);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());
            return registeredUser;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<Users> getUserById(Integer id) {
        logger.debug("Fetching user by ID: {}", id);
        Optional<Users> user = usersRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("Found user with ID: {}", id);
        } else {
            logger.debug("User not found with ID: {}", id);
        }
        return user;
    }

    public Optional<Users> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        Optional<Users> user = usersRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.debug("Found user with email: {}", email);
        } else {
            logger.debug("User not found with email: {}", email);
        }
        return user;
    }

    @Transactional
    public Users updateUser(Integer id, Users userDetails) {
        logger.info("Updating user with ID: {}", id);
        try {
            Users user = usersRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new IllegalArgumentException("User not found");
                    });

            logger.debug("Updating user details for ID: {}", id);
            user.setName(userDetails.getName());
            user.setAddress(userDetails.getAddress());

            if (userDetails.getPassword() != null) {
                logger.debug("Updating password for user ID: {}", id);
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            Users updatedUser = usersRepository.save(user);
            logger.info("User ID {} updated successfully", id);
            return updatedUser;
        } catch (Exception e) {
            logger.error("Error updating user ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteUser(Integer id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            usersRepository.deleteById(id);
            logger.info("User ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting user ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public List<Users> getAllUsers() {
        logger.debug("Fetching all users");
        List<Users> users = usersRepository.findAll();
        logger.debug("Found {} users in database", users.size());
        return users;
    }
}