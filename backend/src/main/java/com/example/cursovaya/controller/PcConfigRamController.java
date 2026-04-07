package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.PcConfigRamRequest;
import com.example.cursovaya.DTO.response.PcConfigRamResponse;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.PcConfigRamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("pc-config-ram")
public class PcConfigRamController {
    private static final Logger log = Logger.getLogger(PcConfigRamController.class.getName());

    private final PcConfigRamService pcConfigRamService;

    public PcConfigRamController(PcConfigRamService pcConfigRamService) {
        this.pcConfigRamService = pcConfigRamService;
    }

    @PatchMapping("{id}/up")
    public ResponseEntity<PcConfigRamResponse> updatePcConfigRam(@PathVariable long id,
                                                                   @RequestBody PcConfigRamRequest pcConfigRamRequest,
                                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "updatePcConfigRam id " + id);
        return ResponseEntity.ok(pcConfigRamService.updatePcConfigRam(id, pcConfigRamRequest, principal.getUserId()));
    }

    @DeleteMapping("{id}/del")
    public ResponseEntity<PcConfigRamResponse> deletePcConfigRam(@PathVariable Long id,
                                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "Delete PcConfigRam Request");
        return ResponseEntity.ok(pcConfigRamService.deletePcConfigRam(id, principal.getUserId()));
    }

    @PostMapping()
    public ResponseEntity<PcConfigRamResponse> createPcConfigRam(@RequestBody PcConfigRamRequest pcConfigRam,
                                                                 @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "creating PcConfigRam");
        return ResponseEntity.ok(pcConfigRamService.createPcConfigRam(pcConfigRam, principal.getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PcConfigRamResponse> getPcConfigRamById(@PathVariable Long id) {
        log.log(Level.INFO, "calling getPcConfigRamById with id: " + id);
        return ResponseEntity.ok(pcConfigRamService.getPcConfigRamById(id));
    }

    @GetMapping()
    public ResponseEntity<List<PcConfigRamResponse>> getAllPcConfigRam() {
        log.log(Level.INFO, "calling getAllPcConfigRam");
        return ResponseEntity.ok(pcConfigRamService.getAllPcConfigRam());
    }
}
