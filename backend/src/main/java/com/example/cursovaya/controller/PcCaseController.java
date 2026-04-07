package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.PcCaseResponse;
import com.example.cursovaya.entity.PcCase;
import com.example.cursovaya.service.PcCaseService;
import com.example.cursovaya.service.ProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/pc-case")
public class PcCaseController {
    private static final Logger log = Logger.getLogger(PcCaseController.class.getName());

    private final PcCaseService pcCaseService;

    public PcCaseController(PcCaseService pcCaseService) {

        this.pcCaseService = pcCaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PcCaseResponse> getPcCaseById(@PathVariable Long id) {
        log.log(Level.INFO, "called method getPcCaseById");
        PcCaseResponse   pcCase = pcCaseService.getPcCaseById(id);
        return ResponseEntity.ok(pcCase);
    }

    @GetMapping
    public ResponseEntity<List<PcCaseResponse>> getAllPcCase() {
        log.log(Level.INFO, "called method getAllPcCase");
        List<PcCaseResponse> pcCaseList = pcCaseService.getAllPcCase();
        return ResponseEntity.ok(pcCaseList);
    }
}
