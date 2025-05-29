package com.example.demo.controller;

import com.example.demo.model.Orders;
import com.example.demo.service.OrdersService;
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
class OrdersControllerTest {

    @Mock
    private OrdersService ordersService;

    @InjectMocks
    private OrdersController ordersController;

    @Test
    void createOrder_Success() {
        // Arrange
        Integer userId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        String address = "Test Address";
        Orders order = new Orders();
        order.setId(1);

        when(ordersService.createOrder(userId, productIds, address))
                .thenReturn(order);

        // Act
        ResponseEntity<Orders> response = ordersController.createOrder(userId, productIds, address);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void createOrder_Failure() {
        // Arrange
        Integer userId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        String address = "Test Address";

        when(ordersService.createOrder(userId, productIds, address))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<Orders> response = ordersController.createOrder(userId, productIds, address);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getOrdersByUserId_Success() {
        // Arrange
        Integer userId = 1;
        Orders order1 = new Orders();
        order1.setId(1);
        Orders order2 = new Orders();
        order2.setId(2);

        when(ordersService.getOrdersByUserId(userId))
                .thenReturn(Arrays.asList(order1, order2));

        // Act
        List<Orders> orders = ordersController.getOrdersByUserId(userId);

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    void getOrderById_Found() {
        // Arrange
        Integer orderId = 1;
        Orders order = new Orders();
        order.setId(orderId);

        when(ordersService.getOrderById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        ResponseEntity<Orders> response = ordersController.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().getId());
    }

    @Test
    void getOrderById_NotFound() {
        // Arrange
        Integer orderId = 1;

        when(ordersService.getOrderById(orderId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Orders> response = ordersController.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        Integer orderId = 1;
        String newStatus = "SHIPPED";
        Orders updatedOrder = new Orders();
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(newStatus);

        when(ordersService.updateOrderStatus(orderId, newStatus))
                .thenReturn(updatedOrder);

        // Act
        ResponseEntity<Orders> response = ordersController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newStatus, response.getBody().getStatus());
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        Integer orderId = 1;

        doNothing().when(ordersService).cancelOrder(orderId);

        // Act
        ResponseEntity<Void> response = ordersController.cancelOrder(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ordersService, times(1)).cancelOrder(orderId);
    }
}