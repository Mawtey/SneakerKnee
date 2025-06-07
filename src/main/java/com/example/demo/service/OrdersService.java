package com.example.demo.service;

import com.example.demo.model.Orders;
import com.example.demo.model.Products;
import com.example.demo.model.Users;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final UsersService usersService;

    public OrdersService(OrdersRepository ordersRepository,
                         ProductsRepository productsRepository,
                         UsersService usersService, EmailService emailService) {
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
        this.usersService = usersService;
        this.emailService = emailService;
        logger.info("OrdersService initialized");
    }

    @Transactional
    public Orders createOrder(Integer userId, List<Integer> productIds, String deliveryAddress) {
        logger.info("Creating order for user ID: {}, products: {}, address: {}",
                userId, productIds, deliveryAddress);

        try {
            Users user = usersService.getUserById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new IllegalArgumentException("User not found");
                    });

            BigDecimal totalPrice = BigDecimal.ZERO;
            logger.debug("Calculating total price for {} products", productIds.size());

            for (Integer productId : productIds) {
                Products product = productsRepository.findById(productId)
                        .orElseThrow(() -> {
                            logger.error("Product not found with ID: {}", productId);
                            return new IllegalArgumentException("Product not found: " + productId);
                        });
                totalPrice = totalPrice.add(product.getPrice());
            }
            logger.debug("Total price calculated: {}", totalPrice);

            Orders order = Orders.builder()
                    .user(user)
                    .status("CREATED")
                    .totalPrice(totalPrice)
                    .deliveryAddress(deliveryAddress)
                    .productIds(productIds)
                    .build();

            logger.debug("Processing product quantities for order");
            for (Integer productId : productIds) {
                productsRepository.decreaseProductQuantity(productId, 1);
                logger.trace("Decreased quantity for product ID: {}", productId);
            }

            Orders savedOrder = ordersRepository.save(order);
            logger.info("Order created successfully with ID: {}", savedOrder.getId());
            return savedOrder;

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw e;
        }
    }


    public List<Orders> getOrdersByUserId(Integer userId) {
        logger.debug("Fetching orders for user ID: {}", userId);
        List<Orders> orders = ordersRepository.findByUserId(userId);
        logger.debug("Found {} orders for user ID: {}", orders.size(), userId);
        return orders;
    }

    public List<Orders> getOrdersByStatus(String status) {
        logger.debug("Fetching orders with status: {}", status);
        List<Orders> orders = ordersRepository.findByStatus(status);
        logger.debug("Found {} orders with status: {}", orders.size(), status);
        return orders;
    }

    public Optional<Orders> getOrderById(Integer id) {
        logger.debug("Fetching order by ID: {}", id);
        Optional<Orders> order = ordersRepository.findById(id);
        if (order.isPresent()) {
            logger.debug("Found order with ID: {}", id);
        } else {
            logger.debug("Order not found with ID: {}", id);
        }
        return order;
    }

//    @Transactional
//    public Orders updateOrderStatus(Integer id, String status) {
//        logger.info("Updating status for order ID: {} to {}", id, status);
//        try {
//            Orders order = ordersRepository.findById(id)
//                    .orElseThrow(() -> {
//                        logger.error("Order not found with ID: {}", id);
//                        return new IllegalArgumentException("Order not found");
//                    });
//
//            order.setStatus(status);
//            Orders updatedOrder = ordersRepository.save(order);
//            logger.info("Order ID: {} status updated to {}", id, status);
//            return updatedOrder;
//
//        } catch (Exception e) {
//            logger.error("Error updating order status: {}", e.getMessage());
//            throw e;
//        }
//    }
private final EmailService emailService;

    @Transactional
    public Orders updateOrderStatus(Integer id, String status) {
        logger.info("Updating status for order ID: {} to {}", id, status);
        try {
            Orders order = ordersRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Order not found with ID: {}", id);
                        return new IllegalArgumentException("Order not found");
                    });

            order.setStatus(status);
            Orders updatedOrder = ordersRepository.save(order);

            // Отправка письма
            String email = order.getUser().getEmail();
            emailService.sendOrderStatusUpdate(email, id.toString(), status);

            logger.info("Order ID: {} status updated to {}", id, status);
            return updatedOrder;

        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public void cancelOrder(Integer id) {
        logger.info("Cancelling order with ID: {}", id);
        try {
            Orders order = ordersRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Order not found with ID: {}", id);
                        return new IllegalArgumentException("Order not found");
                    });

            logger.debug("Returning products to stock for order ID: {}", id);
            for (Integer productId : order.getProductIds()) {
                Products product = productsRepository.findById(productId)
                        .orElseThrow(() -> {
                            logger.error("Product not found with ID: {}", productId);
                            return new IllegalArgumentException("Product not found: " + productId);
                        });
                product.setQuantity(product.getQuantity() + 1);
                productsRepository.save(product);
                logger.trace("Increased quantity for product ID: {}", productId);
            }

            ordersRepository.deleteById(id);
            logger.info("Order ID: {} cancelled successfully", id);

        } catch (Exception e) {
            logger.error("Error cancelling order: {}", e.getMessage());
            throw e;
        }
    }
}