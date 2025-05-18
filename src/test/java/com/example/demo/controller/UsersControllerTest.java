package com.example.demo.controller;

import com.example.demo.model.Users;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdUserId;
    private static final String TEST_EMAIL = "testuser@example.com";

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/users";
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        Users user = new Users();
        user.setName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setPassword("password123");
        user.setAddress("123 Test Street");

        ResponseEntity<Users> response = restTemplate.postForEntity(
                getBaseUrl() + "/register",
                user,
                Users.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(TEST_EMAIL, response.getBody().getEmail());

        createdUserId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testGetUserById() {
        ResponseEntity<Users> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdUserId,
                Users.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdUserId, response.getBody().getId());
        assertEquals(TEST_EMAIL, response.getBody().getEmail());
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        Users updatedUser = new Users();
        updatedUser.setName("Updated Name");
        updatedUser.setAddress("456 Updated Street");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Users> requestEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<Users> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdUserId,
                HttpMethod.PUT,
                requestEntity,
                Users.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("456 Updated Street", response.getBody().getAddress());
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + createdUserId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Users> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdUserId,
                Users.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @Order(5)
    void testRegisterUserWithExistingEmail() {
        Users user = new Users();
        user.setName("Duplicate User");
        user.setEmail("duplicate@example.com");
        user.setPassword("password123");

        restTemplate.postForEntity(getBaseUrl() + "/register", user, Users.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/register",
                user,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body is null");
        assertTrue(response.getBody().contains("Email already exists"));
    }

}