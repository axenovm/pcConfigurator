package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createAt;

    //связи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcConfiguration_id", nullable = false)
    @ToString.Exclude
    private PcConfiguration pcConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
}
