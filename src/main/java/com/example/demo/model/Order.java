package com.example.demo.model;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private Integer userId;
    private String status;
    private Double totalPrice;
    private String deliveryAddress;
    private List<Integer> productIds;
}