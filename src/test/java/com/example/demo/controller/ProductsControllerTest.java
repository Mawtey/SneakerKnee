package com.example.demo.controller;

import com.example.demo.model.Products;
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
class ProductsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdProductId;
    private static final String TEST_PRODUCT_NAME = "Test Product";

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    @Test
    @Order(1)
    void testCreateProduct() {
        Products product = new Products();
        product.setName(TEST_PRODUCT_NAME);
        product.setBrand("Test Brand");
        product.setSize("42");
        product.setColor("Black");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setDescription("Test description");

        ResponseEntity<Products> response = restTemplate.postForEntity(
                getBaseUrl(),
                product,
                Products.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(TEST_PRODUCT_NAME, response.getBody().getName());

        createdProductId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testGetProductById() {
        ResponseEntity<Products> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdProductId,
                Products.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdProductId, response.getBody().getId());
        assertEquals(TEST_PRODUCT_NAME, response.getBody().getName());
    }

    @Test
    @Order(3)
    void testGetProductsByBrand() {
        ResponseEntity<Products[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/brand/Test Brand",
                Products[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("Test Brand", response.getBody()[0].getBrand());
    }

    @Test
    @Order(4)
    void testUpdateProduct() {

        ResponseEntity<Products> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdProductId,
                Products.class);

        Products existingProduct = getResponse.getBody();
        assertNotNull(existingProduct);
        Products updatedProduct = new Products();
        updatedProduct.setName("Updated Product Name");
        updatedProduct.setPrice(new BigDecimal("109.99"));
        updatedProduct.setBrand(existingProduct.getBrand());
        updatedProduct.setSize(existingProduct.getSize());
        updatedProduct.setColor(existingProduct.getColor());
        updatedProduct.setQuantity(existingProduct.getQuantity());
        updatedProduct.setDescription(existingProduct.getDescription());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Products> requestEntity = new HttpEntity<>(updatedProduct, headers);

        ResponseEntity<Products> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdProductId,
                HttpMethod.PUT,
                requestEntity,
                Products.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected OK status but got: " + response.getStatusCode() +
                        ", body: " + response.getBody());

        assertNotNull(response.getBody());
        assertEquals("Updated Product Name", response.getBody().getName());
        assertEquals(new BigDecimal("109.99"), response.getBody().getPrice());
    }

    @Test
    @Order(5)
    void testDeleteProduct() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + createdProductId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Products> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdProductId,
                Products.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }


}