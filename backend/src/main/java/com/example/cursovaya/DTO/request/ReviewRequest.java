package com.example.cursovaya.DTO.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long userId;

    private Long pcId;

    private Integer rating;

    private String comment;
}
