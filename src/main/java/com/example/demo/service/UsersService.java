package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }

    @Transactional
    public Users registerUser(Users user) {
        logger.info("Attempting to register new user with email: {}", user.getEmail());
        if (usersRepository.existsByEmail(user.getEmail())) {
            logger.error("Registration failed - email already exists: {}", user.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        user.setName(StringEscapeUtils.escapeHtml4(user.getName()));
        if (user.getAddress() != null) {
            user.setAddress(StringEscapeUtils.escapeHtml4(user.getAddress()));
        }

        logger.debug("Hashing password for user: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        Users registeredUser = usersRepository.save(user);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return registeredUser;
    }

    @Transactional(readOnly = true)
    public Optional<Users> getUserById(Integer id) {
        logger.debug("Fetching user by ID: {}", id);
        Optional<Users> user = usersRepository.findById(id);
        user.ifPresentOrElse(
                u -> logger.debug("Found user with ID: {}", id),
                () -> logger.debug("User not found with ID: {}", id)
        );
        return user;
    }


    @Transactional(readOnly = true)
    public Optional<Users> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        Optional<Users> user = usersRepository.findByEmail(email);
        user.ifPresentOrElse(
                u -> logger.debug("Found user with email: {}", email),
                () -> logger.debug("User not found with email: {}", email)
        );
        return user;
    }

    @Transactional
    public Users updateUser(Integer id, Users userDetails) {
        logger.info("Updating user with ID: {}", id);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found");
                });

        logger.debug("Updating user details for ID: {}", id);
        user.setName(userDetails.getName());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            logger.debug("Updating password for user ID: {}", id);
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        Users updatedUser = usersRepository.save(user);
        logger.info("User ID {} updated successfully", id);
        return updatedUser;
    }

    @Transactional
    public void deleteUser(Integer id) {
        logger.info("Deleting user with ID: {}", id);
        if (!usersRepository.existsById(id)) {
            logger.error("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }
        usersRepository.deleteById(id);
        logger.info("User ID {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        logger.debug("Fetching all users");
        List<Users> users = usersRepository.findAll();
        logger.debug("Found {} users in database", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }
}