package com.example.cursovaya.service;

import com.example.cursovaya.DTO.response.PowerUnitResponse;
import com.example.cursovaya.entity.PowerUnit;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.PowerUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerUnitService {
    private final PowerUnitRepository powerUnitRepository;

    public PowerUnitService(PowerUnitRepository powerUnitRepository) {

        this.powerUnitRepository = powerUnitRepository;
    }

    public PowerUnitResponse getPowerUnitById(Long id){
        return convertToResponse(powerUnitRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PowerUnit with id = " + id + "not found")));
    }

    public List<PowerUnitResponse> getAllPowerUnits(){

        return powerUnitRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public PowerUnitResponse convertToResponse(PowerUnit powerUnit){
        PowerUnitResponse powerUnitResponse = new PowerUnitResponse();

        powerUnitResponse.setId(powerUnit.getPowerUnitId());
        powerUnitResponse.setColour(powerUnit.getColor());
        powerUnitResponse.setModel(powerUnit.getModel());
        powerUnitResponse.setPower(powerUnit.getPower());
        powerUnitResponse.setPrice(powerUnit.getPrice());

        return powerUnitResponse;
    }
}
