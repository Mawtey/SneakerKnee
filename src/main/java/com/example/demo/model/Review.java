package com.example.demo.model;

import lombok.Data;
import lombok.Setter;

@Data
public class Review {
    private Integer id;
    private Integer productId;
    private Integer userId;

    @Setter
    private Integer rating;

    private String comment;

    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
}