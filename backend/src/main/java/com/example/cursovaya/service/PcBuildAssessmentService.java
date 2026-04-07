package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.PcBuildAssessmentResponse;
import com.example.cursovaya.entity.*;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.PcConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PcBuildAssessmentService {
    private final PcConfigurationRepository pcConfigurationRepository;
    private final CompatibilityService compatibilityService;

    public PcBuildAssessmentService(PcConfigurationRepository pcConfigurationRepository,
                                    CompatibilityService compatibilityService) {
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.compatibilityService = compatibilityService;
    }

    @Transactional
    public PcBuildAssessmentResponse getFullAssessment(Long pcConfigurationId, Long userId) {

        PcConfiguration config = pcConfigurationRepository.findById(pcConfigurationId)
                .orElseThrow(() -> new ResourceNotFoundException("not found config with id: " + pcConfigurationId));

        if (!config.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("user does not have config with id: " + pcConfigurationId);
        }

        // оценка
        PcBuildAssessmentResponse assessment = calculateAssessment(config);

        // рекомендации
        String performanceLevel = determinePerformanceLevel(assessment.getPerformanceScore());
        List<String> recommendations = generateRecommendations(config, assessment);

        assessment.setPerformanceLevel(performanceLevel);
        assessment.setRecommendations(recommendations);

        return assessment;
    }

    private PcBuildAssessmentResponse calculateAssessment(PcConfiguration config) {
        PcBuildAssessmentResponse assessment = new PcBuildAssessmentResponse();
        assessment.setPcConfigurationId(config.getId());
        assessment.setBuildName(config.getBuildName());
        assessment.setTotalPrice(config.getTotalPrice());
        assessment.setCreatedAtAssessment(LocalDateTime.now());

        int performanceScore = calculatePerformanceScore(config);
        int totalTdp = compatibilityService.calculateTotalTdp(config);

        assessment.setPerformanceScore(performanceScore);
        assessment.setTotalTdp(totalTdp);
        assessment.setBottleneckAnalysis(analyzeBottleneck(config));

        return assessment;
    }

    private int calculatePerformanceScore(PcConfiguration config) {
        int score = 60;

        if (config.getVideoCard() != null && config.getVideoCard().getTdp() > 300) score += 20;
        if (config.getProcessor() != null && config.getProcessor().getTdp() > 100) score += 15;
        if (config.getPcConfigRams() != null) {
            int totalRam = config.getPcConfigRams().stream()
                    .mapToInt(PcConfigRam::getRamQuantity).sum();
            if (totalRam >= 4) score += 10;
        }

        return Math.min(100, score);
    }

    private String analyzeBottleneck(PcConfiguration config) {
        if (config.getProcessor() == null || config.getVideoCard() == null) {
            return "Недостаточно данных для анализа узких мест.";
        }

        Processor cpu = config.getProcessor();
        VideoCard gpu = config.getVideoCard();

        int cpuScore = estimateComponentScore(cpu.getTdp(), "CPU");
        int gpuScore = estimateComponentScore(gpu.getTdp(), "GPU");

        int difference = Math.abs(cpuScore - gpuScore);

        if (difference >= 35) {
            if (cpuScore < gpuScore) {
                return "Сильный bottleneck по процессору. CPU заметно ограничивает видеокарту в играх.";
            } else {
                return "Сильный bottleneck по видеокарте. GPU ограничивает потенциал процессора.";
            }
        }
        else if (difference >= 20) {
            if (cpuScore < gpuScore) {
                return "Небольшой bottleneck по процессору. В требовательных играх CPU может немного ограничивать FPS.";
            } else {
                return "Небольшой bottleneck по видеокарте.";
            }
        }
        else {
            return "Баланс между процессором и видеокартой хороший. Узких мест не выявлено.";
        }
    }

    private int estimateComponentScore(int tdp, String type) {
        if ("CPU".equals(type)) {
            if (tdp >= 140) return 95;
            if (tdp >= 100) return 82;
            if (tdp >= 65)  return 68;
            return 50;
        } else { // GPU
            if (tdp >= 400) return 98;
            if (tdp >= 300) return 88;
            if (tdp >= 200) return 75;
            if (tdp >= 120) return 62;
            return 45;
        }
    }

    private String determinePerformanceLevel(int score) {
        if (score >= 90) return "Флагманский уровень";
        if (score >= 75) return "Высокий уровень";
        if (score >= 60) return "Средний уровень";
        if (score >= 40) return "Минимальный уровень";
        return "Начальный уровень";
    }

    private List<String> generateRecommendations(PcConfiguration config, PcBuildAssessmentResponse assessment) {
        List<String> recs = new ArrayList<>();
        int score = assessment.getPerformanceScore();

        if (score >= 90) {
            recs.add("Отличная флагманская сборка. Она хорошо сбалансирована и готова к самым требовательным задачам");
        }
        else if (score >= 75) {
            recs.add("Сборка соответствует высокому уровню");
            recs.add("Для достижения флагманского уровня можно рассмотреть апгрейд видеокарты или процессора");
        }
        else if (score >= 60) {
            recs.add("Сборка соответствует средним требованиям для игр и работы");
            recs.add("До высокого уровня рекомендуется улучшить видеокарту или увеличить объём ОЗУ до 32 ГБ");
        }
        else if (score >= 40) {
            recs.add("Сборка соответствует минимальным требованиям");
            recs.add("Для комфортной работы рекомендуется значительно улучшить процессор и видеокарту");
        }
        else {
            recs.add("Сборка находится на начальном уровне");
            recs.add("Рекомендуется начать апгрейд с видеокарты и процессора");
        }

        int totalTdp = compatibilityService.calculateTotalTdp(config);
        if (totalTdp > config.getPowerUnit().getPower() * 0.75) {
            recs.add("Блок питания работает с небольшим запасом. Рассмотрите более мощный БП для стабильности");
        }

        return recs;
    }
}
