package com.example.demo.repository;
import com.example.demo.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {

    List<Products> findByBrand(String brand);

    List<Products> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Modifying
    @Transactional
    @Query("UPDATE Products p SET p.quantity = p.quantity - :quantity WHERE p.id = :productId AND p.quantity >= :quantity")
    int decreaseProductQuantity(@Param("productId") Integer productId, @Param("quantity") int quantity);
}
