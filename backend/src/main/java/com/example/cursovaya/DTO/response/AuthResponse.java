package com.example.cursovaya.DTO.response;

import com.example.cursovaya.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;

    private Long id;

    private String nickname;

    private String login;

    private String description;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.login = user.getLogin();
        this.description = user.getDescription();
    }
}

