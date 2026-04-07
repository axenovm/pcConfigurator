package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.PcBuildAssessmentResponse;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.PcBuildAssessmentService;
import io.jsonwebtoken.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class PcBuildAssessmentController {

    Logger log = Logger.getLogger(PcBuildAssessmentController.class.getName());

    private final PcBuildAssessmentService pcBuildAssessmentService;

    public PcBuildAssessmentController(PcBuildAssessmentService pcBuildAssessmentService) {
        this.pcBuildAssessmentService = pcBuildAssessmentService;
    }

    @GetMapping("/assessment/{pcId}")
    public ResponseEntity<PcBuildAssessmentResponse> getPcBuildAssessment(@PathVariable("pcId") Long pcId,
                                                                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        PcBuildAssessmentResponse pcBuildAssessmentResponse = pcBuildAssessmentService.getFullAssessment(pcId, userDetailsImpl.getUserId());
        return ResponseEntity.ok(pcBuildAssessmentResponse);
    }
}
