package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.request.PcConfigStorageRequest;
import com.example.cursovaya.DTO.response.PcConfigStorageResponse;
import com.example.cursovaya.security.UserDetailsImpl;
import com.example.cursovaya.service.PcConfigStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("pc-config-storage")
public class PcConfigStorageController {
    private static final Logger log = Logger.getLogger(PcConfigStorageController.class.getName());

    private final PcConfigStorageService pcConfigStorageService;

    public PcConfigStorageController(PcConfigStorageService pcConfigStorageService) {
        this.pcConfigStorageService = pcConfigStorageService;
    }

    @PatchMapping("{id}/up")
    public ResponseEntity<PcConfigStorageResponse> updatePcConfigStorage(@PathVariable long id,
                                                                         @RequestBody PcConfigStorageRequest request,
                                                                         @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "updatePcConfigStorage id " + id);
        return ResponseEntity.ok(pcConfigStorageService.updatePcConfigStorage(id, request, principal.getUserId()));
    }

    @DeleteMapping("{id}/del")
    public ResponseEntity<PcConfigStorageResponse> deletePcConfigStorage(@PathVariable Long id,
                                                                         @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "Delete PcConfigStorage Request");
        return ResponseEntity.ok(pcConfigStorageService.deletePcConfigStorage(id, principal.getUserId()));
    }

    @PostMapping()
    public ResponseEntity<PcConfigStorageResponse> createPcConfigStorage(@RequestBody PcConfigStorageRequest request,
                                                                         @AuthenticationPrincipal UserDetailsImpl principal) {
        log.log(Level.INFO, "creating PcConfigStorage");
        return ResponseEntity.ok(pcConfigStorageService.createPcConfigStorage(request, principal.getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PcConfigStorageResponse> getPcConfigStorageById(@PathVariable Long id) {
        log.log(Level.INFO, "getPcConfigStorageById id: " + id);
        return ResponseEntity.ok(pcConfigStorageService.getPcConfigStorageById(id));
    }

    @GetMapping()
    public ResponseEntity<List<PcConfigStorageResponse>> getAllPcConfigStorage() {
        log.log(Level.INFO, "getAllPcConfigStorage");
        return ResponseEntity.ok(pcConfigStorageService.getAllPcConfigStorage());
    }
}
