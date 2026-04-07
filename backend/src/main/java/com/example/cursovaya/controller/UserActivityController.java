package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.UserActivityResponse;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.UserActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("user-activity")
public class UserActivityController {
    private static final Logger logger = Logger.getLogger(UserActivityController.class.getName());

    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<UserActivityResponse>> getUserActivity(@PathVariable Long userId,
                                                                      @AuthenticationPrincipal UserDetailsImpl principal) {
        if (!principal.getUserId().equals(userId)) {
            throw new AccessDeniedException("cannot view another user's activity log");
        }
        logger.info("getUserActivity with id: " + userId);
        return ResponseEntity.ok(userActivityService.getUserActivity(userId));
    }
}
