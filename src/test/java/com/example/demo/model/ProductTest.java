package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testProductCreation() {
        Product product = new Product();
        product.setId(1);
        product.setName("Air Max");
        product.setBrand("Nike");
        product.setSize("42");
        product.setColor("Black");
        product.setPrice(120.0);
        product.setDescription("Comfortable sneakers");
        product.setQuantity(10);

        assertEquals(1, product.getId());
        assertEquals("Air Max", product.getName());
        assertEquals("Nike", product.getBrand());
        assertEquals("42", product.getSize());
        assertEquals("Black", product.getColor());
        assertEquals(120.0, product.getPrice(), 0.001);
        assertEquals("Comfortable sneakers", product.getDescription());
        assertEquals(10, product.getQuantity());
    }

    @Test
    public void testProductToString() {
        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");

        assertNotNull(product.toString());
        assertTrue(product.toString().contains("Test Product"));
    }
}