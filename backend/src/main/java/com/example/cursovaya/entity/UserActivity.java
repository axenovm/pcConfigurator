package com.example.cursovaya.entity;

import com.example.cursovaya.enums.ActionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_activity_id")
    private Long userActivityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String activityInfo;

    //связи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
}
