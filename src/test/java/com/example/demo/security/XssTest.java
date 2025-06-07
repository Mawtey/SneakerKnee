package com.example.demo.security;

import com.example.demo.model.Products;
import com.example.demo.repository.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class XssTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductsRepository productsRepository;

    @Test
    public void testXssInProductName() throws Exception {
        String xssPayload = "<script>alert('XSS')</script>";

        String productJson = String.format(
                "{\"name\":\"%s\",\"brand\":\"test\",\"size\":\"42\",\"color\":\"black\",\"price\":10.0,\"quantity\":10}",
                xssPayload
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated());

        Products savedProduct = productsRepository.findAll()
                .stream()
                .filter(p -> p.getName().contains("script"))
                .findFirst()
                .orElseThrow();
        assertFalse(savedProduct.getName().contains("<script>"));
        assertTrue(savedProduct.getName().contains("&lt;script&gt;"));
    }

    @Test
    public void testXssInUserAddress() throws Exception {
        String xssPayload = "\"<img src=x onerror=alert(1)>\"";

        String userJson = String.format("{\"name\":\"test\",\"email\":\"testxss@example.com\",\"password\":\"password\",\"address\":%s}", xssPayload);

        MvcResult result = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertFalse(response.contains("&lt;img src=x onerror=alert(1)&gt;"));
    }

    @Test
    public void testXssInOrderDeliveryAddress() throws Exception {
        String xssPayload = "<svg/onload=alert('XSS')>";
        Products product = new Products();
        product.setName("Safe Product");
        product.setBrand("Test");
        product.setSize("42");
        product.setColor("black");
        product.setPrice(BigDecimal.TEN);
        product.setQuantity(10);
        product = productsRepository.save(product);

        mockMvc.perform(post("/api/orders")
                        .param("userId", "1")
                        .param("productIds", String.valueOf(product.getId()))
                        .param("deliveryAddress", xssPayload))
                .andExpect(status().isCreated());

    }
}