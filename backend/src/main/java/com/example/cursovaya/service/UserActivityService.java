package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.UserActivityResponse;
import com.example.cursovaya.entity.User;
import com.example.cursovaya.entity.UserActivity;
import com.example.cursovaya.enums.ActionType;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.UserActivityRepository;
import com.example.cursovaya.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;

    public UserActivityService(UserActivityRepository userActivityRepository,
                               UserRepository userRepository) {
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
    }

    public List<UserActivityResponse> getUserActivity(Long userId) {
        return userActivityRepository.findByUser_IdOrderByTimestampDesc(userId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void addUserActivity(ActionType actionType, String activityInfo, Long actorUserId) {
        User user = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id = " + actorUserId));
        UserActivity userActivity = new UserActivity();
        userActivity.setActionType(actionType);
        userActivity.setActivityInfo(activityInfo != null ? activityInfo : "");
        userActivity.setUser(user);
        userActivity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(userActivity);
    }

    public UserActivityResponse convertToResponse(UserActivity userActivity) {
        UserActivityResponse userActivityResponse = new UserActivityResponse();

        userActivityResponse.setActivityInfo(userActivity.getActivityInfo());
        userActivityResponse.setTimestamp(userActivity.getTimestamp());
        userActivityResponse.setActionType(userActivity.getActionType());

        return  userActivityResponse;
    }
}
