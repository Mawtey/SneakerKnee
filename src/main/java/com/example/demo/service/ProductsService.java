package com.example.demo.service;

import org.apache.commons.text.StringEscapeUtils;
import com.example.demo.model.Products;
import com.example.demo.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private static final Logger logger = LoggerFactory.getLogger(ProductsService.class);
    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
        logger.info("ProductsService initialized");
    }

    @Transactional
    public Products createProduct(Products product) {
        logger.info("Creating new product: {}", product);
        try {
            product.setName(StringEscapeUtils.escapeHtml4(product.getName()));
            product.setBrand(StringEscapeUtils.escapeHtml4(product.getBrand()));
            product.setColor(StringEscapeUtils.escapeHtml4(product.getColor()));
            product.setSize(StringEscapeUtils.escapeHtml4(product.getSize()));
            if (product.getDescription() != null) {
                product.setDescription(StringEscapeUtils.escapeHtml4(product.getDescription()));
            }

            Products savedProduct = productsRepository.save(product);
            logger.info("Product created successfully with ID: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage());
            throw e;
        }
    }

    public List<Products> getAllProducts() {
        logger.debug("Fetching all products");
        List<Products> products = productsRepository.findAll();
        logger.debug("Found {} products", products.size());
        return products;
    }

    public Optional<Products> getProductById(Integer id) {
        logger.debug("Fetching product by ID: {}", id);
        Optional<Products> product = productsRepository.findById(id);
        if (product.isPresent()) {
            logger.debug("Found product with ID: {}", id);
        } else {
            logger.debug("Product not found with ID: {}", id);
        }
        return product;
    }

    public List<Products> getProductsByBrand(String brand) {
        logger.debug("Fetching products by brand: {}", brand);
        List<Products> products = productsRepository.findByBrand(brand);
        logger.debug("Found {} products for brand '{}'", products.size(), brand);
        return products;
    }

    public List<Products> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("Fetching products in price range: {} - {}", minPrice, maxPrice);
        List<Products> products = productsRepository.findByPriceBetween(minPrice, maxPrice);
        logger.debug("Found {} products in specified price range", products.size());
        return products;
    }

    @Transactional
    public Products updateProduct(Integer id, Products productDetails) {
        logger.info("Updating product ID {} with new details: {}", id, productDetails);
        try {
            Products product = productsRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Product not found with ID: {}", id);
                        return new IllegalArgumentException("Product not found");
                    });

            logger.debug("Updating product fields for ID: {}", id);
            product.setName(productDetails.getName());
            product.setBrand(productDetails.getBrand());
            product.setSize(productDetails.getSize());
            product.setColor(productDetails.getColor());
            product.setPrice(productDetails.getPrice());
            product.setDescription(productDetails.getDescription());
            product.setQuantity(productDetails.getQuantity());

            Products updatedProduct = productsRepository.save(product);
            logger.info("Product ID {} updated successfully", id);
            return updatedProduct;
        } catch (Exception e) {
            logger.error("Error updating product ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteProduct(Integer id) {
        logger.info("Deleting product with ID: {}", id);
        try {
            productsRepository.deleteById(id);
            logger.info("Product ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting product ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void decreaseProductQuantity(Integer productId, int quantity) {
        logger.info("Decreasing quantity for product ID {} by {}", productId, quantity);
        try {
            Products product = productsRepository.findById(productId)
                    .orElseThrow(() -> {
                        logger.error("Product not found with ID: {}", productId);
                        return new IllegalArgumentException("Product not found");
                    });

            if (product.getQuantity() < quantity) {
                logger.error("Not enough stock for product ID {}. Requested: {}, Available: {}",
                        productId, quantity, product.getQuantity());
                throw new IllegalArgumentException("Not enough products in stock");
            }

            product.setQuantity(product.getQuantity() - quantity);
            productsRepository.save(product);
            logger.info("Quantity decreased for product ID {}. New quantity: {}",
                    productId, product.getQuantity());
        } catch (Exception e) {
            logger.error("Error decreasing quantity for product ID {}: {}", productId, e.getMessage());
            throw e;
        }
    }
}