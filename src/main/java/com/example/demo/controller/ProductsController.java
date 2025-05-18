package com.example.demo.controller;

import com.example.demo.model.Products;
import com.example.demo.service.ProductsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productsService;
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping
    public ResponseEntity<Products> createProduct(@Valid @RequestBody Products product) {
        logger.info("Attempt to create new product: {}", product);
        try {
            Products createdProduct = productsService.createProduct(product);
            logger.info("Product created successfully with ID: {}", createdProduct.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<Products> getAllProducts() {
        logger.debug("Fetching all products");
        List<Products> products = productsService.getAllProducts();
        logger.debug("Found {} products", products.size());
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Integer id) {
        logger.debug("Fetching product by ID: {}", id);
        return productsService.getProductById(id)
                .map(product -> {
                    logger.debug("Found product with ID: {}", id);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    logger.warn("Product with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/brand/{brand}")
    public List<Products> getProductsByBrand(@PathVariable String brand) {
        logger.debug("Fetching products by brand: {}", brand);
        List<Products> products = productsService.getProductsByBrand(brand);
        logger.debug("Found {} products for brand {}", products.size(), brand);
        return products;
    }

    @GetMapping("/price-range")
    public List<Products> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        logger.debug("Fetching products in price range: {} - {}", min, max);
        List<Products> products = productsService.getProductsByPriceRange(min, max);
        logger.debug("Found {} products in specified price range", products.size());
        return products;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Products> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody Products productDetails) {
        logger.info("Attempt to update product ID {} with data: {}", id, productDetails);
        try {
            Products updatedProduct = productsService.updateProduct(id, productDetails);
            logger.info("Product ID {} updated successfully", id);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating product ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        logger.info("Attempt to delete product with ID: {}", id);
        try {
            productsService.deleteProduct(id);
            logger.info("Product ID {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting product ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}