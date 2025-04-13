package com.example.demo.model;

import lombok.Data;
import lombok.Setter;

@Data
public class Review {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private Integer rating;
    private String comment;

}