package com.example.cursovaya.config;

import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.JwtService;
import com.example.cursovaya.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7).trim();
            final String userLogin = jwtService.extractUsername(jwt);
            final Long userIdFromToken = jwtService.extractUserId(jwt);

            if (userLogin == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Authentication existing = SecurityContextHolder.getContext().getAuthentication();
            if (existing != null
                    && existing.isAuthenticated()
                    && !(existing instanceof AnonymousAuthenticationToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = this.userService.loadUserByUsername(userLogin);

            if (jwtService.validateToken(jwt, userDetails)) {
                if (userIdFromToken != null) {
                    Long userIdFromDetails = ((UserDetailsImpl) userDetails).getUserId();
                    if (userIdFromDetails == null
                            || Long.compare(userIdFromToken, userIdFromDetails) != 0) {
                        log.warn("User ID in token does not match user details for user: {}", userLogin);
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("Successfully authenticated user: {}", userLogin);
            }
        } catch (Exception e) {
            log.error("JWT Authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}