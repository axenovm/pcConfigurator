package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.PcConfigStorageRequest;
import com.example.cursovaya.DTO.response.PcConfigStorageResponse;
import com.example.cursovaya.entity.PcConfigStorage;
import com.example.cursovaya.entity.PcConfiguration;
import com.example.cursovaya.entity.StorageDevice;
import com.example.cursovaya.enums.ActionType;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.exception.ValidationException;
import com.example.cursovaya.repository.PcConfigStorageRepository;
import com.example.cursovaya.repository.PcConfigurationRepository;
import com.example.cursovaya.repository.StorageDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PcConfigStorageService {
    private static final int MAX_STORAGE_DEVICES_PER_BUILD = 6;

    private final PcConfigStorageRepository pcConfigStorageRepository;
    private final StorageDeviceRepository storageDeviceRepository;
    private final PcConfigurationRepository pcConfigurationRepository;
    private final UserActivityService userActivityService;

    public PcConfigStorageService(PcConfigStorageRepository pcConfigStorageRepository,
                                  StorageDeviceRepository storageDeviceRepository,
                                  PcConfigurationRepository pcConfigurationRepository,
                                  UserActivityService userActivityService) {
        this.pcConfigStorageRepository = pcConfigStorageRepository;
        this.storageDeviceRepository = storageDeviceRepository;
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.userActivityService = userActivityService;
    }

    @Transactional
    public PcConfigStorageResponse createPcConfigStorage(PcConfigStorageRequest request, Long actorUserId) {
        validateQuantity(request.getQuantity());

        PcConfiguration config = pcConfigurationRepository.findById(request.getPcConfigurationId())
                .orElseThrow(() -> new ResourceNotFoundException("pc config not found with id = " + request.getPcConfigurationId()));
        if (!config.getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot add storage to another user's build");
        }
        validateStorageLimit(config, request.getQuantity(), null);
        StorageDevice storageDevice = storageDeviceRepository.findById(request.getStorageDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("storage device not found with id = " + request.getStorageDeviceId()));

        PcConfigStorage toSave = new PcConfigStorage();
        toSave.setPcConfiguration(config);
        toSave.setStorageDevice(storageDevice);
        toSave.setQuantity(request.getQuantity());
        toSave.setPrice(storageDevice.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        PcConfigStorage saved = pcConfigStorageRepository.save(toSave);
        config.setTotalPrice(config.getTotalPrice().add(saved.getPrice()));
        pcConfigurationRepository.save(config);
        userActivityService.addUserActivity(ActionType.ADD_STORAGE,
                "Добавлен накопитель к сборке «" + config.getBuildName() + "»", actorUserId);
        return convertToResponse(saved);
    }

    @Transactional
    public PcConfigStorageResponse updatePcConfigStorage(Long id, PcConfigStorageRequest request, Long actorUserId) {
        PcConfigStorage current = pcConfigStorageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pc config storage not found with id = " + id));
        if (!current.getPcConfiguration().getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot modify storage of another user's build");
        }
        BigDecimal oldPositionPrice = current.getPrice();

        PcConfiguration targetConfig = current.getPcConfiguration();
        if (request.getPcConfigurationId() != null) {
            targetConfig = pcConfigurationRepository.findById(request.getPcConfigurationId())
                    .orElseThrow(() -> new ResourceNotFoundException("pc config not found with id = " + request.getPcConfigurationId()));
            if (!targetConfig.getUser().getId().equals(actorUserId)) {
                throw new AccessDeniedException("cannot move storage to another user's build");
            }
            current.setPcConfiguration(targetConfig);
        }

        if (request.getStorageDeviceId() != null) {
            StorageDevice storage = storageDeviceRepository.findById(request.getStorageDeviceId())
                    .orElseThrow(() -> new ResourceNotFoundException("storage device not found with id = " + request.getStorageDeviceId()));
            current.setStorageDevice(storage);
        }

        if (request.getQuantity() != null) {
            validateQuantity(request.getQuantity());
            current.setQuantity(request.getQuantity());
        }
        validateStorageLimit(targetConfig, current.getQuantity(), current.getConfigStorageId());

        current.setPrice(current.getStorageDevice().getPrice().multiply(BigDecimal.valueOf(current.getQuantity())));
        PcConfigStorage updated = pcConfigStorageRepository.save(current);

        PcConfiguration config = updated.getPcConfiguration();
        config.setTotalPrice(config.getTotalPrice().subtract(oldPositionPrice).add(updated.getPrice()));
        pcConfigurationRepository.save(config);

        return convertToResponse(updated);
    }

    @Transactional
    public PcConfigStorageResponse deletePcConfigStorage(Long id, Long actorUserId) {
        PcConfigStorage current = pcConfigStorageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pc config storage not found with id = " + id));
        if (!current.getPcConfiguration().getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot delete storage of another user's build");
        }

        PcConfiguration config = current.getPcConfiguration();
        config.setTotalPrice(config.getTotalPrice().subtract(current.getPrice()));
        pcConfigurationRepository.save(config);

        PcConfigStorageResponse response = convertToResponse(current);
        pcConfigStorageRepository.deleteById(id);
        return response;
    }

    public PcConfigStorageResponse getPcConfigStorageById(Long id) {
        PcConfigStorage current = pcConfigStorageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pc config storage not found with id = " + id));
        return convertToResponse(current);
    }

    public List<PcConfigStorageResponse> getAllPcConfigStorage() {
        return pcConfigStorageRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new ValidationException("storage quantity cannot be less than 1");
        }
    }

    private void validateStorageLimit(PcConfiguration config, Integer requestedQuantity, Long currentStorageRowId) {
        int totalStorageDevices = pcConfigStorageRepository.findByPcConfigurationId(config.getId())
                .stream()
                .filter(storage -> currentStorageRowId == null || !storage.getConfigStorageId().equals(currentStorageRowId))
                .mapToInt(PcConfigStorage::getQuantity)
                .sum();
        int newTotalStorageDevices = totalStorageDevices + requestedQuantity;
        if (newTotalStorageDevices > MAX_STORAGE_DEVICES_PER_BUILD) {
            throw new ValidationException("storage device limit exceeded: maximum " + MAX_STORAGE_DEVICES_PER_BUILD + " per build");
        }
    }

    public PcConfigStorageResponse convertToResponse(PcConfigStorage pcConfigStorage) {
        PcConfigStorageResponse response = new PcConfigStorageResponse();
        response.setId(pcConfigStorage.getConfigStorageId());
        response.setPcConfigurationId(pcConfigStorage.getPcConfiguration().getId());
        response.setQuantity(pcConfigStorage.getQuantity());
        response.setPrice(pcConfigStorage.getPrice());
        response.setStorageModel(pcConfigStorage.getStorageDevice().getModel());
        response.setStorageCapacity(pcConfigStorage.getStorageDevice().getHDDCapacity());
        return response;
    }
}
