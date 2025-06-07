package com.example.demo.security;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SqlInjectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void testSqlInjectionInEmailField() throws Exception {

        String maliciousEmail = "test@example.com' OR '1'='1";
        mockMvc.perform(get("/api/users")
                        .param("email", maliciousEmail))
                .andExpect(status().isOk());

        assertFalse(usersRepository.existsByEmail(maliciousEmail));
    }

    @Test
    public void testSqlInjectionInProductSearch() throws Exception {

        String maliciousInput = "1' OR '1'='1' --";

        mockMvc.perform(get("/api/products")
                        .param("search", maliciousInput))
                .andExpect(status().isOk());
    }

    @Test
    public void testSqlInjectionInOrderStatus() throws Exception {

        String maliciousStatus = "CREATED' OR '1'='1";

        mockMvc.perform(get("/api/orders/status/" + maliciousStatus))
                .andExpect(status().isOk());
    }
}