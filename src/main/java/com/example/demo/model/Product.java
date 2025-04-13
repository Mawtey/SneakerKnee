package com.example.demo.model;

import lombok.Data;

@Data
public class Product {
    private Integer id;
    private String name;
    private String brand;
    private String size;
    private String color;
    private Double price;
    private String description;
    private Integer quantity;
}