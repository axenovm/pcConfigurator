package com.example.cursovaya.DTO.response;

import com.example.cursovaya.enums.ActionType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserActivityResponse {
    private ActionType actionType;

    private String activityInfo;

    private LocalDateTime timestamp;
}
