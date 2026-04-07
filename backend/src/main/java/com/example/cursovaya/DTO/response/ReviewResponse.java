package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long userId;

    private String authorNickname;

    private Long pcId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
