package com.example.demo.controller;

import com.example.demo.model.Orders;
import com.example.demo.model.Products;
import com.example.demo.model.Users;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdersControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdOrderId;
    private static Integer testUserId;
    private static Integer testProductId1;
    private static Integer testProductId2;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/orders";
    }

    private String getUsersUrl() {
        return "http://localhost:" + port + "/api/users";
    }

    private String getProductsUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    @BeforeEach
    void setUpTestData() {
        if (testUserId == null) {
            Users user = new Users();
            user.setName("Order Test User");
            user.setEmail("ordertest@example.com");
            user.setPassword("password123");

            Users createdUser = restTemplate.postForEntity(
                    getUsersUrl() + "/register",
                    user,
                    Users.class).getBody();

            testUserId = createdUser.getId();
        }

        if (testProductId1 == null) {
            Products product1 = new Products();
            product1.setName("Order Test Product 1");
            product1.setPrice(new BigDecimal("50.00"));
            product1.setQuantity(10);

            Products createdProduct1 = restTemplate.postForEntity(
                    getProductsUrl(),
                    product1,
                    Products.class).getBody();

            testProductId1 = createdProduct1.getId();
        }

        if (testProductId2 == null) {
            Products product2 = new Products();
            product2.setName("Order Test Product 2");
            product2.setPrice(new BigDecimal("75.00"));
            product2.setQuantity(5);

            Products createdProduct2 = restTemplate.postForEntity(
                    getProductsUrl(),
                    product2,
                    Products.class).getBody();

            testProductId2 = createdProduct2.getId();
        }
    }

    @Test
    @Order(1)
    void testCreateOrder() {
        String productIds = testProductId1 + "," + testProductId2;

        ResponseEntity<Orders> response = restTemplate.postForEntity(
                getBaseUrl() + "?userId={userId}&productIds={productIds}&deliveryAddress={address}",
                HttpEntity.EMPTY,
                Orders.class,
                testUserId,
                productIds,
                "123 Delivery Street");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(new BigDecimal("125.00"), response.getBody().getTotalPrice());
        assertEquals(2, response.getBody().getProductIds().size());

        createdOrderId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testGetOrderById() {
        ResponseEntity<Orders> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdOrderId,
                Orders.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdOrderId, response.getBody().getId());
        assertEquals("CREATED", response.getBody().getStatus());
    }

    @Test
    @Order(3)
    void testGetOrdersByUserId() {
        ResponseEntity<Orders[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/user/" + testUserId,
                Orders[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(4)
    void testUpdateOrderStatus() {
        ResponseEntity<Orders> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdOrderId + "/status?status=PROCESSING",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Orders.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PROCESSING", response.getBody().getStatus());
    }

    @Test
    @Order(5)
    void testCancelOrder() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + createdOrderId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Orders> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdOrderId,
                Orders.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }


}