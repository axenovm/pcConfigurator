package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.PowerUnitResponse;
import com.example.cursovaya.entity.PowerUnit;
import com.example.cursovaya.repository.PowerUnitRepository;
import com.example.cursovaya.service.PowerUnitService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/power-unit")
public class PowerUnitController {
    private static final Logger log = Logger.getLogger(PowerUnitController.class.getName());

    private final PowerUnitService powerUnitService;

    public PowerUnitController(PowerUnitService powerUnitService){
        this.powerUnitService = powerUnitService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowerUnitResponse> getPowerUnitById(@PathVariable Long id){
        log.info("called method getPowerUnitById");
        PowerUnitResponse powerUnit = powerUnitService.getPowerUnitById(id);
        return ResponseEntity.ok(powerUnit);
    }

    @GetMapping
    public ResponseEntity<List<PowerUnitResponse>> getAllPowerUnits(){
        log.info("called method getAllPowerUnits");
        List<PowerUnitResponse> powerUnits = powerUnitService.getAllPowerUnits();
        return ResponseEntity.ok(powerUnits);
    }
}
