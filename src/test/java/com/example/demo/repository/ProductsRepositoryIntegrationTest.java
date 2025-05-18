package com.example.demo.repository;

import com.example.demo.model.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductsRepositoryIntegrationTest {

    @Autowired
    private ProductsRepository productRepository;

    @BeforeEach
    void setUp() {
        Products p1 = new Products();
        p1.setName("Air Max");
        p1.setBrand("Nike");
        p1.setPrice(new BigDecimal("120.00"));
        p1.setQuantity(10);
        productRepository.save(p1);

        Products p2 = new Products();
        p2.setName("Ultraboost");
        p2.setBrand("Adidas");
        p2.setPrice(new BigDecimal("150.00"));
        p2.setQuantity(5);
        productRepository.save(p2);

        Products p3 = new Products();
        p3.setName("React");
        p3.setBrand("Nike");
        p3.setPrice(new BigDecimal("110.00"));
        p3.setQuantity(8);
        productRepository.save(p3);
    }

    @Test
    void testFindByBrand() {
        List<Products> nikeProducts = productRepository.findByBrand("Nike");
        assertEquals(2, nikeProducts.size());
        assertEquals("Air Max", nikeProducts.get(0).getName());
    }

    @Test
    void testFindByPriceBetween() {
        List<Products> products = productRepository.findByPriceBetween(
                new BigDecimal("115.00"),
                new BigDecimal("140.00"));

        assertEquals(1, products.size());
        assertEquals("Air Max", products.get(0).getName());
    }

    @Test
    void testSaveProduct() {
        Products newProduct = new Products();
        newProduct.setName("New Balance");
        newProduct.setBrand("New Balance");
        newProduct.setPrice(new BigDecimal("99.99"));
        newProduct.setQuantity(15);

        Products saved = productRepository.save(newProduct);
        assertNotNull(saved.getId());
        assertEquals("New Balance", saved.getBrand());
    }
}