package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.ReviewRequest;
import com.example.cursovaya.DTO.response.ReviewResponse;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("review")
public class ReviewController {
    private static final Logger log = Logger.getLogger(ReviewController.class.getName());

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("add")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest reviewRequest,
                                                    @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "addReview");
        return ResponseEntity.ok(reviewService.addReview(reviewRequest, principal.getUserId()));
    }

    @GetMapping("{pcId}")
    public ResponseEntity<List<ReviewResponse>> getReviewForPcConfig(@PathVariable Long pcId) {
        log.log(Level.INFO, "getReviewForPcConfig");
        return ResponseEntity.ok(reviewService.getReviewsForPcConfig(pcId));
    }
}
