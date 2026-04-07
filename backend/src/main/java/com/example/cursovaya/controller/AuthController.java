package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.AuthRequest;
import com.example.cursovaya.DTO.request.UserRegisterRequest;
import com.example.cursovaya.DTO.response.AuthResponse;
import com.example.cursovaya.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return  ResponseEntity.ok(authService.register(userRegisterRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return  ResponseEntity.ok(authService.login(authRequest));
    }
}
