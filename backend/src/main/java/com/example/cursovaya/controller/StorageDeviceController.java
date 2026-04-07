package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.StorageDeviceResponse;
import com.example.cursovaya.repository.StorageDeviceRepository;
import com.example.cursovaya.service.StorageDeviceService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("storage-device")
public class StorageDeviceController {
    private static final Logger log = Logger.getLogger(StorageDeviceController.class.getName());

    private final StorageDeviceService storageDeviceService;

    public  StorageDeviceController(StorageDeviceService storageDeviceService) {
        this.storageDeviceService = storageDeviceService;
    }

    @GetMapping("{id}")
    public ResponseEntity<StorageDeviceResponse> getStorageDeviceById(@PathVariable Long id) {
        log.info("called getStorageDeviceById");
        return ResponseEntity.ok(storageDeviceService.getStorageDeviceById(id));
    }

    @GetMapping()
    public ResponseEntity<List<StorageDeviceResponse>> getAllStorageDevice() {
        log.info("called getAllStorageDevice");
        return ResponseEntity.ok(storageDeviceService.getAllStorageDevice());
    }
}
