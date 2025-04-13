package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("qwerty");
        user.setAddress("Test Address");

        assertEquals(1, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("qwerty", user.getPassword());
        assertEquals("Test Address", user.getAddress());
    }

    @Test
    public void testUserEquality() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(1);
        user2.setName("User 2");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        // Проверка неравенства
        User user3 = new User();
        user3.setId(2);
        assertNotEquals(user1, user3);
    }
}