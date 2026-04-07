package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.RamModuleResponse;
import com.example.cursovaya.entity.RamModule;
import com.example.cursovaya.repository.RamModuleRepository;
import com.example.cursovaya.service.RamModuleService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/ram-module")
public class RamModuleController {
    private final RamModuleService ramModuleService;

    private static final Logger log = Logger.getLogger(RamModuleController.class.getName());

    public RamModuleController(RamModuleService ramModuleService) {
        this.ramModuleService = ramModuleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RamModuleResponse> getRamModuleBiId(@PathVariable long id) {
        log.log(Level.INFO, "called method getRamModuleById with id = " + id);
        RamModuleResponse ramModule = ramModuleService.getRamModuleBiId(id);
        return ResponseEntity.ok(ramModule);
    }

    @GetMapping()
    public ResponseEntity<List<RamModuleResponse>> getAllRamModules() {
        log.log(Level.INFO, "called method getAllRamModules");
        List<RamModuleResponse> ramModules = ramModuleService.getAllRamModules();
        return ResponseEntity.ok(ramModules);
    }
}
