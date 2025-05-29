package com.example.demo.controller;

import com.example.demo.model.Products;
import com.example.demo.service.ProductsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ProductsService productsService;

    @InjectMocks
    private ProductsController productsController;

    @Test
    void createProduct_Success() {
        // Arrange
        Products product = new Products();
        product.setName("Test Product");
        Products savedProduct = new Products();
        savedProduct.setId(1);
        savedProduct.setName("Test Product");

        when(productsService.createProduct(product)).thenReturn(savedProduct);

        // Act
        ResponseEntity<Products> response = productsController.createProduct(product);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        Products product1 = new Products();
        product1.setId(1);
        Products product2 = new Products();
        product2.setId(2);

        when(productsService.getAllProducts())
                .thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Products> products = productsController.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    void getProductById_Found() {
        // Arrange
        Integer productId = 1;
        Products product = new Products();
        product.setId(productId);

        when(productsService.getProductById(productId))
                .thenReturn(Optional.of(product));

        // Act
        ResponseEntity<Products> response = productsController.getProductById(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        Integer productId = 1;

        when(productsService.getProductById(productId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Products> response = productsController.getProductById(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProductsByBrand_Success() {
        // Arrange
        String brand = "Nike";
        Products product1 = new Products();
        product1.setId(1);
        product1.setBrand(brand);
        Products product2 = new Products();
        product2.setId(2);
        product2.setBrand(brand);

        when(productsService.getProductsByBrand(brand))
                .thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Products> products = productsController.getProductsByBrand(brand);

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> brand.equals(p.getBrand())));
    }

    @Test
    void getProductsByPriceRange_Success() {
        // Arrange
        BigDecimal min = new BigDecimal("10.00");
        BigDecimal max = new BigDecimal("100.00");
        Products product1 = new Products();
        product1.setId(1);
        product1.setPrice(new BigDecimal("50.00"));
        Products product2 = new Products();
        product2.setId(2);
        product2.setPrice(new BigDecimal("75.00"));

        when(productsService.getProductsByPriceRange(min, max))
                .thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Products> products = productsController.getProductsByPriceRange(min, max);

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        Integer productId = 1;
        Products productDetails = new Products();
        productDetails.setName("Updated Product");
        Products updatedProduct = new Products();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");

        when(productsService.updateProduct(productId, productDetails))
                .thenReturn(updatedProduct);

        // Act
        ResponseEntity<Products> response = productsController.updateProduct(productId, productDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Product", response.getBody().getName());
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        Integer productId = 1;

        doNothing().when(productsService).deleteProduct(productId);

        // Act
        ResponseEntity<Void> response = productsController.deleteProduct(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productsService, times(1)).deleteProduct(productId);
    }
}