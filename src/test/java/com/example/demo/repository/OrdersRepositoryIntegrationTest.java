package com.example.demo.repository;

import com.example.demo.model.Orders;
import com.example.demo.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrdersRepositoryIntegrationTest {

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private UsersRepository userRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        Orders order1 = new Orders();
        order1.setStatus("PENDING");
        order1.setTotalPrice(new BigDecimal("100.00"));
        order1.setDeliveryAddress("Address 1");
        order1.setProductIds(Arrays.asList(1, 2));
        order1.setUser(testUser);
        orderRepository.save(order1);

        Orders order2 = new Orders();
        order2.setStatus("COMPLETED");
        order2.setTotalPrice(new BigDecimal("200.00"));
        order2.setDeliveryAddress("Address 2");
        order2.setProductIds(Arrays.asList(3));
        order2.setUser(testUser);
        orderRepository.save(order2);
    }

    @Test
    void testFindByUserId() {
        List<Orders> orders = orderRepository.findByUserId(testUser.getId());
        assertEquals(2, orders.size());
        assertEquals("PENDING", orders.get(0).getStatus());
    }

    @Test
    void testFindByStatus() {
        List<Orders> completedOrders = orderRepository.findByStatus("COMPLETED");
        assertEquals(1, completedOrders.size());
        assertEquals(0, new BigDecimal("200.00").compareTo(completedOrders.get(0).getTotalPrice()));
    }

    @Test
    void testSaveOrder() {
        Orders newOrder = new Orders();
        newOrder.setStatus("NEW");
        newOrder.setTotalPrice(new BigDecimal("50.00"));
        newOrder.setDeliveryAddress("New Address");
        newOrder.setProductIds(Arrays.asList(4, 5));
        newOrder.setUser(testUser);

        Orders saved = orderRepository.save(newOrder);
        assertNotNull(saved.getId());
        assertEquals(2, saved.getProductIds().size());
    }
}