package com.example.demo.repository;

import com.example.demo.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsersRepositoryIntegrationTest {

    @Autowired
    private UsersRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        Users user = new Users();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setAddress("Test Address");

        Users savedUser = userRepository.save(user);
        Optional<Users> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testEmailUniqueness() {
        Users user1 = new Users();
        user1.setEmail("unique@example.com");
        user1.setName("User 1");
        user1.setPassword("pass1");
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setEmail("unique@example.com");
        user2.setName("User 2");
        user2.setPassword("pass2");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }

    @Test
    void testFindByEmail() {
        Users user = new Users();
        user.setEmail("findme@example.com");
        user.setName("Find Me");
        user.setPassword("password");
        userRepository.save(user);

        Optional<Users> found = userRepository.findByEmail("findme@example.com");
        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getName());
    }
}