package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.StorageDeviceRequest;
import com.example.cursovaya.DTO.response.StorageDeviceResponse;
import com.example.cursovaya.entity.StorageDevice;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.StorageDeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageDeviceService {
    private final StorageDeviceRepository storageDeviceRepository;

    public StorageDeviceService(StorageDeviceRepository storageDeviceRepository) {
        this.storageDeviceRepository = storageDeviceRepository;
    }

    public StorageDeviceResponse getStorageDeviceById(Long id) {
        return convertToResponse(storageDeviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found storage device with id: " + id)));
    }

    public List<StorageDeviceResponse> getAllStorageDevice() {
        return storageDeviceRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public StorageDeviceResponse convertToResponse(StorageDevice storageDevice) {
        StorageDeviceResponse storageDeviceResponse = new StorageDeviceResponse();

        storageDeviceResponse.setId(storageDevice.getStorageDeviceId());
        storageDeviceResponse.setHddCapacity(storageDevice.getHDDCapacity());
        storageDeviceResponse.setModel(storageDevice.getModel());
        storageDeviceResponse.setPrice(storageDevice.getPrice());

        return storageDeviceResponse;
    }
}
