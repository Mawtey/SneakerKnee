package com.example.demo.service;

import com.example.demo.model.Products;
import com.example.demo.repository.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductsRepository productsRepository;

    @InjectMocks
    private ProductsService productsService;

    @Test
    void createProduct_Success() {
        Products product = new Products();
        product.setName("Nike Air Max");

        when(productsRepository.save(any(Products.class))).thenReturn(product);

        Products created = productsService.createProduct(product);

        assertNotNull(created);
        assertEquals("Nike Air Max", created.getName());
        verify(productsRepository).save(product);
    }

    @Test
    void getProductById_Found() {
        Products product = new Products();
        product.setId(1);

        when(productsRepository.findById(1)).thenReturn(Optional.of(product));

        Optional<Products> found = productsService.getProductById(1);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getId());
    }

    @Test
    void getProductById_NotFound() {
        when(productsRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Products> found = productsService.getProductById(1);

        assertFalse(found.isPresent());
    }

    @Test
    void getProductsByBrand_Success() {
        Products p1 = new Products();
        p1.setBrand("Nike");
        Products p2 = new Products();
        p2.setBrand("Nike");

        when(productsRepository.findByBrand("Nike")).thenReturn(Arrays.asList(p1, p2));

        List<Products> products = productsService.getProductsByBrand("Nike");

        assertEquals(2, products.size());
        products.forEach(p -> assertEquals("Nike", p.getBrand()));
    }

    @Test
    void getProductsByPriceRange_Success() {
        Products p1 = new Products();
        p1.setPrice(new BigDecimal("99.99"));
        Products p2 = new Products();
        p2.setPrice(new BigDecimal("149.99"));

        when(productsRepository.findByPriceBetween(
                new BigDecimal("90.00"),
                new BigDecimal("150.00"))
        ).thenReturn(Arrays.asList(p1, p2));

        List<Products> products = productsService.getProductsByPriceRange(
                new BigDecimal("90.00"),
                new BigDecimal("150.00"));

        assertEquals(2, products.size());
    }

    @Test
    void updateProduct_Success() {
        Products existing = new Products();
        existing.setId(1);
        existing.setName("Old Name");

        Products update = new Products();
        update.setName("New Name");
        update.setBrand("Nike");

        when(productsRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productsRepository.save(any(Products.class))).thenReturn(update);

        Products updated = productsService.updateProduct(1, update);

        assertEquals("New Name", updated.getName());
        assertEquals("Nike", updated.getBrand());
    }

    @Test
    void updateProduct_NotFound() {
        when(productsRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            productsService.updateProduct(1, new Products());
        });
    }

    @Test
    void deleteProduct_Success() {
        doNothing().when(productsRepository).deleteById(1);

        productsService.deleteProduct(1);

        verify(productsRepository).deleteById(1);
    }

    @Test
    void decreaseProductQuantity_Success() {
        Products product = new Products();
        product.setId(1);
        product.setQuantity(10);

        when(productsRepository.findById(1)).thenReturn(Optional.of(product));
        when(productsRepository.save(any(Products.class))).thenReturn(product);

        productsService.decreaseProductQuantity(1, 3);

        assertEquals(7, product.getQuantity());
    }

    @Test
    void decreaseProductQuantity_NotEnoughStock() {
        Products product = new Products();
        product.setId(1);
        product.setQuantity(2);

        when(productsRepository.findById(1)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> {
            productsService.decreaseProductQuantity(1, 3);
        });
    }
}