package com.example.cursovaya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    private String description;

    //надо переделать
    @Column(nullable = false)
    private String password;

    //связи
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<PcConfiguration> pcConfigurations;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<UserActivity> userActivities;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Review> reviews;
}
