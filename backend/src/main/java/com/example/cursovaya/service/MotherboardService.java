package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.MotherboardRequest;
import com.example.cursovaya.DTO.response.MotherboardResponse;
import com.example.cursovaya.controller.MotherboardController;
import com.example.cursovaya.entity.Motherboard;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.MotherboardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class MotherboardService {
    private static final Logger log = Logger.getLogger(MotherboardService.class.getName());

    private final MotherboardRepository motherboardRepository;

    public MotherboardService(MotherboardRepository motherboardRepository) {
        this.motherboardRepository = motherboardRepository;
    }

    public MotherboardResponse getMotherboardById(Long id)
    {
        return  convertToResponse(motherboardRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Motherboard with id = " + id + " not found")));
    }

    public List<MotherboardResponse> getAllMotherboard()
    {
        return  motherboardRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public MotherboardResponse convertToResponse(Motherboard  motherboard) {
        MotherboardResponse motherboardResponse = new MotherboardResponse();

        motherboardResponse.setId(motherboard.getMotherboardId());
        motherboardResponse.setColour(motherboard.getColour());
        motherboardResponse.setFormFactor(motherboard.getFormFactor());
        motherboardResponse.setModel(motherboard.getModel());
        motherboardResponse.setPrice(motherboard.getPrice());
        motherboardResponse.setSeries(motherboard.getSeries());
        motherboardResponse.setSocket(motherboard.getSocket());

        return motherboardResponse;
    }
}
