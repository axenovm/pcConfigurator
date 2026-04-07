package com.example.cursovaya.DTO.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String nickname;

    private String login;

    private String password;
}
