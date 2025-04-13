package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        Order order = new Order();
        order.setId(1);
        order.setUserId(100);
        order.setStatus("Pending");
        order.setTotalPrice(250.0);
        order.setDeliveryAddress("123 Main St");
        order.setProductIds(Arrays.asList(1, 2, 3));

        assertEquals(1, order.getId());
        assertEquals(100, order.getUserId());
        assertEquals("Pending", order.getStatus());
        assertEquals(250.0, order.getTotalPrice(), 0.001);
        assertEquals("123 Main St", order.getDeliveryAddress());
        assertEquals(Arrays.asList(1, 2, 3), order.getProductIds());
    }

    @Test
    public void testOrderStatusChange() {
        Order order = new Order();
        order.setStatus("Pending");
        order.setStatus("Shipped");

        assertEquals("Shipped", order.getStatus());
    }
}