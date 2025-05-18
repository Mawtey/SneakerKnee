package com.example.demo.controller;

import com.example.demo.model.Orders;
import com.example.demo.service.OrdersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping
    public ResponseEntity<Orders> createOrder(
            @RequestParam Integer userId,
            @RequestParam List<Integer> productIds,
            @RequestParam String deliveryAddress) {
        logger.info("Attempt to create order for user: {}, products: {}, address: {}",
                userId, productIds, deliveryAddress);
        try {
            Orders order = ordersService.createOrder(userId, productIds, deliveryAddress);
            logger.info("Order created successfully with ID: {}", order.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public List<Orders> getOrdersByUserId(@PathVariable Integer userId) {
        logger.debug("Fetching orders for user ID: {}", userId);
        return ordersService.getOrdersByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<Orders> getOrdersByStatus(@PathVariable String status) {
        logger.debug("Fetching orders with status: {}", status);
        return ordersService.getOrdersByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Integer id) {
        logger.debug("Fetching order by ID: {}", id);
        return ordersService.getOrderById(id)
                .map(order -> {
                    logger.debug("Found order with ID: {}", id);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    logger.warn("Order with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Orders> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        logger.info("Updating order status for ID: {}, new status: {}", id, status);
        try {
            Orders order = ordersService.updateOrderStatus(id, status);
            logger.info("Order status updated successfully for ID: {}", id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {
        logger.info("Attempt to cancel order with ID: {}", id);
        try {
            ordersService.cancelOrder(id);
            logger.info("Order canceled successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error canceling order: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}