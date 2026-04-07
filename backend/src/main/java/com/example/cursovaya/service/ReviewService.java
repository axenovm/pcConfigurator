package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.ReviewRequest;
import com.example.cursovaya.DTO.response.ReviewResponse;
import com.example.cursovaya.entity.PcConfiguration;
import com.example.cursovaya.entity.Review;
import com.example.cursovaya.entity.User;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.PcConfigurationRepository;
import com.example.cursovaya.repository.ReviewRepository;
import com.example.cursovaya.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PcConfigurationRepository pcConfigurationRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         PcConfigurationRepository pcConfigurationRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.userRepository = userRepository;
    }

    public List<ReviewResponse> getReviewsForPcConfig(Long pcId) {
        pcConfigurationRepository.findById(pcId)
                .orElseThrow(() -> new ResourceNotFoundException("Pc configuration not found: " + pcId));
        return reviewRepository.findByPcConfiguration_IdOrderByCreateAtDesc(pcId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    public ReviewResponse addReview(ReviewRequest reviewRequest, Long authorUserId) {
        User user = userRepository.findById(authorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        PcConfiguration pcConfiguration = pcConfigurationRepository.findById(reviewRequest.getPcId())
                .orElseThrow(() -> new ResourceNotFoundException("Pc configuration not found"));

        if (pcConfiguration.getUser().getId().equals(authorUserId)) {
            throw new AccessDeniedException("Нельзя оставить отзыв на собственную сборку");
        }

        Review reviewToSave = new Review();
        reviewToSave.setPcConfiguration(pcConfiguration);
        reviewToSave.setUser(user);
        reviewToSave.setComment(reviewRequest.getComment());
        reviewToSave.setRating(reviewRequest.getRating());
        reviewToSave.setCreateAt(LocalDateTime.now());

        reviewRepository.save(reviewToSave);

        return convertToResponse(reviewToSave);
    }

    public ReviewResponse convertToResponse(Review review) {
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setUserId(review.getUser().getId());
        reviewResponse.setAuthorNickname(review.getUser().getNickname());
        reviewResponse.setPcId(review.getPcConfiguration().getId());
        reviewResponse.setRating(review.getRating());
        reviewResponse.setComment(review.getComment());
        reviewResponse.setCreatedAt(review.getCreateAt());
        return reviewResponse;
    }
}
