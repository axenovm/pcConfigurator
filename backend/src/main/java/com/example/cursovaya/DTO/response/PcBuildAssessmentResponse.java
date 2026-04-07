package com.example.cursovaya.DTO.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PcBuildAssessmentResponse {
    private Long pcConfigurationId;
    private String buildName;
    private BigDecimal totalPrice;
    private LocalDateTime createdAtAssessment;

    private Integer performanceScore;        // 0-100
    private String bottleneckAnalysis;
    private Integer totalTdp;

    private String performanceLevel;

    private List<String> recommendations;
}
