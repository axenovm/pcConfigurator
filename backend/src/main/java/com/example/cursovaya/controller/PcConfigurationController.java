package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.PcConfigRequest;
import com.example.cursovaya.DTO.response.PcConfigResponse;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.PcConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("pc-configuration")
public class PcConfigurationController {
    private final PcConfigurationService pcConfigurationService;
    private static final Logger log = Logger.getLogger(PcConfigurationController.class.getName());

    public PcConfigurationController(PcConfigurationService pcConfigurationService) {
        this.pcConfigurationService = pcConfigurationService;
    }

    @PatchMapping("{id}/up")
    public ResponseEntity<PcConfigResponse> updatePcConfig(@PathVariable Long id,
                                                           @RequestBody PcConfigRequest pcConfigRequest,
                                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "updatePcConfig with id " + id);
        PcConfigResponse pcConfigResponse = pcConfigurationService.updatePcConfig(id, pcConfigRequest, principal.getUserId());
        return ResponseEntity.ok(pcConfigResponse);
    }

    @DeleteMapping("{id}/del")
    public ResponseEntity<PcConfigResponse> deletePcConfig(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "Delete PcConfigRam Request");
        return ResponseEntity.ok(pcConfigurationService.deletePcConfig(id, principal.getUserId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<PcConfigResponse> getPcConfigurationById(@PathVariable Long id) {
        log.info("called method getPcConfigurationById");
        PcConfigResponse pcConfiguration = pcConfigurationService.getPcConfigurationById(id);
        return ResponseEntity.ok(pcConfiguration);
    }

    @GetMapping()
    public ResponseEntity<List<PcConfigResponse>> getAllPcConfigurations() {
        log.info("called method getAllPcConfiguration");
        List<PcConfigResponse> pcConfigurations = pcConfigurationService.getAllPcConfigurations();
        return ResponseEntity.ok(pcConfigurations);
    }

    @PostMapping()
    public ResponseEntity<PcConfigResponse> createPcConfiguration(@RequestBody PcConfigRequest pcConfigRequest,
                                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        log.info("called method savePcConfiguration");
        PcConfigResponse pcConfigResponse = pcConfigurationService.createPcConfiguration(pcConfigRequest, principal.getUserId());
        return ResponseEntity.ok(pcConfigResponse);
    }
}
