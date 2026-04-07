package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.AuthRequest;
import com.example.cursovaya.DTO.request.UserRegisterRequest;
import com.example.cursovaya.DTO.response.AuthResponse;
import com.example.cursovaya.entity.User;
import com.example.cursovaya.exception.CustomAuthenticationException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.UserRepository;
import com.example.cursovaya.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthResponse register(UserRegisterRequest userDto) {

        if (userRepository.existsByLogin(userDto.getLogin())) {
            throw new ResourceNotFoundException("User already exists");
        }

        User user = new User();
        user.setNickname(userDto.getNickname());
        user.setLogin(userDto.getLogin());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userService.createUser(user);

        UserDetails userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, userRepository.findByLogin(userDto.getLogin()).orElseThrow());
    }

    public AuthResponse login(AuthRequest request) {
        try {
            if (!userRepository.existsByLogin(request.getLogin())) {
                throw new ResourceNotFoundException("User with email " + request.getLogin() + " not found");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return new AuthResponse(token, userRepository.findByLogin(request.getLogin()).orElseThrow());
        } catch (BadCredentialsException exception) {
            throw new CustomAuthenticationException("Invalid password");
        } catch (AuthenticationException exception) {
            throw new CustomAuthenticationException("Authentication failed: " + exception.getMessage());
        }
    }
}
