package com.example.demo.service;

import com.example.demo.model.Orders;
import com.example.demo.model.Products;
import com.example.demo.model.Users;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.repository.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    void createOrder_Success() {
        Users user = new Users();
        user.setId(1);

        Products p1 = new Products();
        p1.setId(101);
        p1.setPrice(new BigDecimal("99.99"));

        Products p2 = new Products();
        p2.setId(102);
        p2.setPrice(new BigDecimal("149.99"));

        when(usersService.getUserById(1)).thenReturn(Optional.of(user));
        when(productsRepository.findById(101)).thenReturn(Optional.of(p1));
        when(productsRepository.findById(102)).thenReturn(Optional.of(p2));
        when(ordersRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Orders order = ordersService.createOrder(1, Arrays.asList(101, 102), "123 Main St");

        assertNotNull(order);
        assertEquals(new BigDecimal("249.98"), order.getTotalPrice());
        assertEquals("CREATED", order.getStatus());
        assertEquals(2, order.getProductIds().size());

        verify(productsRepository, times(2)).decreaseProductQuantity(anyInt(), eq(1));
    }

    @Test
    void createOrder_UserNotFound() {
        when(usersService.getUserById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            ordersService.createOrder(1, Collections.singletonList(101), "123 Main St");
        });
    }

    @Test
    void createOrder_ProductNotFound() {
        Users user = new Users();
        user.setId(1);

        when(usersService.getUserById(1)).thenReturn(Optional.of(user));
        when(productsRepository.findById(101)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            ordersService.createOrder(1, Collections.singletonList(101), "123 Main St");
        });
    }

    @Test
    void getOrdersByUserId_Success() {
        Orders o1 = new Orders();
        Orders o2 = new Orders();

        when(ordersRepository.findByUserId(1)).thenReturn(Arrays.asList(o1, o2));

        List<Orders> orders = ordersService.getOrdersByUserId(1);

        assertEquals(2, orders.size());
    }

    @Test
    void getOrdersByStatus_Success() {
        Orders o1 = new Orders();
        o1.setStatus("CREATED");
        Orders o2 = new Orders();
        o2.setStatus("CREATED");

        when(ordersRepository.findByStatus("CREATED")).thenReturn(Arrays.asList(o1, o2));

        List<Orders> orders = ordersService.getOrdersByStatus("CREATED");

        assertEquals(2, orders.size());
        orders.forEach(o -> assertEquals("CREATED", o.getStatus()));
    }

    @Test
    void updateOrderStatus_Success() {
        Orders order = new Orders();
        order.setId(1);
        order.setStatus("CREATED");

        when(ordersRepository.findById(1)).thenReturn(Optional.of(order));
        when(ordersRepository.save(any(Orders.class))).thenReturn(order);

        Orders updated = ordersService.updateOrderStatus(1, "PROCESSING");

        assertEquals("PROCESSING", updated.getStatus());
    }

    @Test
    void updateOrderStatus_NotFound() {
        when(ordersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            ordersService.updateOrderStatus(1, "PROCESSING");
        });
    }

    @Test
    void cancelOrder_Success() {
        Orders order = new Orders();
        order.setId(1);
        order.setProductIds(Collections.singletonList(101));

        Products product = new Products();
        product.setId(101);
        product.setQuantity(5);

        when(ordersRepository.findById(1)).thenReturn(Optional.of(order));
        when(productsRepository.findById(101)).thenReturn(Optional.of(product));
        doNothing().when(ordersRepository).deleteById(1);

        ordersService.cancelOrder(1);

        assertEquals(6, product.getQuantity());
        verify(ordersRepository).deleteById(1);
    }

    @Test
    void cancelOrder_NotFound() {
        when(ordersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            ordersService.cancelOrder(1);
        });
    }
}