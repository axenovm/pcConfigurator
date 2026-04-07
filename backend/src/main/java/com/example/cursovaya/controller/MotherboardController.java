package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.MotherboardResponse;
import com.example.cursovaya.entity.Motherboard;
import com.example.cursovaya.service.MotherboardService;
import com.example.cursovaya.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/motherboard")
public class MotherboardController {
    private static final Logger log = Logger.getLogger(MotherboardController.class.getName());

    private final MotherboardService motherboardService;

    public MotherboardController(MotherboardService motherboardService) {
        this.motherboardService = motherboardService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotherboardResponse> getMotherboardById(@PathVariable Long id){
        log.log(Level.INFO, "called method getMotherboardById with id = {}", id);
        MotherboardResponse motherboard = motherboardService.getMotherboardById(id);
        return ResponseEntity.ok(motherboard);
    }

    @GetMapping
    public ResponseEntity<List<MotherboardResponse>> getAllMotherboards(){
        log.log(Level.INFO, "called method getAllMotherboards");
        return ResponseEntity.ok(motherboardService.getAllMotherboard());
    }
}
