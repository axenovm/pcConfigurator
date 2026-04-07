package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.PcConfigRequest;
import com.example.cursovaya.DTO.response.PcConfigRamResponse;
import com.example.cursovaya.DTO.response.PcConfigStorageResponse;
import com.example.cursovaya.DTO.response.PcConfigResponse;
import com.example.cursovaya.entity.*;
import com.example.cursovaya.enums.ActionType;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.exception.IncompatibleComponentsException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PcConfigurationService {

    private final PcConfigurationRepository pcConfigurationRepository;
    private final MotherboardRepository motherboardRepository;
    private final PcCaseRepository pcCaseRepository;
    private final PowerUnitRepository powerUnitRepository;
    private final ProcessorRepository processorRepository;
    private final ProcessorCoolingRepository processorCoolingRepository;
    private final UserRepository userRepository;
    private final VideoCardRepository videoCardRepository;
    private final PcConfigRamRepository pcConfigRamRepository;
    private final PcConfigStorageRepository pcConfigStorageRepository;
    private final PcConfigRamService pcConfigRamService;
    private final PcConfigStorageService pcConfigStorageService;
    private final CompatibilityService compatibleService;
    private final UserActivityService userActivityService;

    public PcConfigurationService(PcConfigurationRepository pcConfigurationRepository,
                                  MotherboardRepository motherboardRepository,
                                  PcCaseRepository pcCaseRepository,
                                  PowerUnitRepository powerUnitRepository,
                                  ProcessorRepository processorRepository,
                                  ProcessorCoolingRepository processorCoolingRepository,
                                  UserRepository userRepository,
                                  VideoCardRepository videoCardRepository,
                                  PcConfigRamRepository pcConfigRamRepository,
                                  PcConfigStorageRepository pcConfigStorageRepository,
                                  PcConfigRamService pcConfigRamService,
                                  PcConfigStorageService pcConfigStorageService,
                                  UserActivityService userActivityService,
                                  CompatibilityService compatibleService) {
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.motherboardRepository = motherboardRepository;
        this.pcCaseRepository = pcCaseRepository;
        this.powerUnitRepository = powerUnitRepository;
        this.processorRepository = processorRepository;
        this.processorCoolingRepository = processorCoolingRepository;
        this.userRepository = userRepository;
        this.videoCardRepository = videoCardRepository;
        this.pcConfigRamRepository = pcConfigRamRepository;
        this.pcConfigStorageRepository = pcConfigStorageRepository;
        this.pcConfigRamService = pcConfigRamService;
        this.pcConfigStorageService = pcConfigStorageService;
        this.compatibleService = compatibleService;
        this.userActivityService = userActivityService;
    }

    @Transactional
    public PcConfigResponse updatePcConfig(Long id, PcConfigRequest pcConfigRequest, Long actorUserId) {
        PcConfiguration pcConfiguration = pcConfigurationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PcConfiguration with id " + id + " not found to update")
        );
        if (!pcConfiguration.getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot modify build of another user");
        }
        boolean changed = false;
        if (pcConfigRequest.getProcessorId() != null) {
            Processor newProcessor = processorRepository.findById(pcConfigRequest.getProcessorId())
                    .orElseThrow(() -> new ResourceNotFoundException("processor with id " + pcConfigRequest.getProcessorId() + " not found to update"));
            pcConfiguration.setProcessor(newProcessor);
            changed = true;
        }
        if (pcConfigRequest.getMotherboardId() != null) {
            Motherboard newMotherboard = motherboardRepository.findById(pcConfigRequest.getMotherboardId())
                    .orElseThrow(() -> new ResourceNotFoundException("motherboard with id " + pcConfigRequest.getMotherboardId() + " not found to update"));
            pcConfiguration.setMotherboard(newMotherboard);
            changed = true;
        }
        if (pcConfigRequest.getVideoCardId() != null) {
            VideoCard newVideoCard = videoCardRepository.findById(pcConfigRequest.getVideoCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("videoCard with id " + pcConfigRequest.getVideoCardId() + " not found to update"));
            pcConfiguration.setVideoCard(newVideoCard);
            changed = true;
        }
        if (pcConfigRequest.getPcCaseId() != null) {
            PcCase newPcCase = pcCaseRepository.findById(pcConfigRequest.getPcCaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("pcCase with id " + pcConfigRequest.getPcCaseId() + " not found to update"));
            pcConfiguration.setPcCase(newPcCase);
            changed = true;
        }
        if (pcConfigRequest.getPowerUnitId() != null) {
            PowerUnit newPowerUnit = powerUnitRepository.findById(pcConfigRequest.getPowerUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("powerUnit with id " + pcConfigRequest.getPowerUnitId() + " not found to update"));
            pcConfiguration.setPowerUnit(newPowerUnit);
            changed = true;
        }
        if (pcConfigRequest.getProcessorCoolingId() != null) {
            ProcessorCooling newCooling = processorCoolingRepository.findById(pcConfigRequest.getProcessorCoolingId())
                    .orElseThrow(() -> new ResourceNotFoundException("processor cooling with id " + pcConfigRequest.getProcessorCoolingId() + " not found to update"));
            pcConfiguration.setProcessorCooling(newCooling);
            changed = true;
        }
        if (pcConfigRequest.getName() != null) {
            pcConfiguration.setBuildName(pcConfigRequest.getName());
            changed = true;
        }
        if (!changed) {
            return convertToResponse(pcConfiguration);
        }

        BigDecimal basePrice = getTotalPriceWithoutRAMAndStorage(pcConfiguration);
        BigDecimal ramTotal = pcConfigRamRepository.findByPcConfigurationId(pcConfiguration.getId())
                .stream()
                .map(PcConfigRam::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal storageTotal = pcConfigStorageRepository.findByPcConfigurationId(pcConfiguration.getId())
                .stream()
                .map(PcConfigStorage::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalPrice = basePrice.add(ramTotal).add(storageTotal);
        pcConfiguration.setTotalPrice(newTotalPrice);

        checkCompatibility(pcConfiguration);
        userActivityService.addUserActivity(ActionType.BUILD_UPDATE,
                "Изменена сборка «" + pcConfiguration.getBuildName() + "»", actorUserId);
        pcConfigurationRepository.save(pcConfiguration);
        return convertToResponse(pcConfiguration);
    }

    @Transactional
    public PcConfigResponse deletePcConfig(Long id, Long actorUserId) {
        PcConfiguration pcConfiguration = pcConfigurationRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("PcConfigRam Not Found"));
        if (!pcConfiguration.getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot delete build of another user");
        }

        List<PcConfigRam> allRamModules = pcConfigRamRepository.findByPcConfigurationId(pcConfiguration.getId());
        //сделать каскадное удаление
        for (PcConfigRam pcConfigRam : allRamModules) {
            pcConfigRamService.deletePcConfigRam(pcConfigRam.getConfigRamId(), actorUserId);
        }
        List<PcConfigStorage> allStoragePositions = pcConfigStorageRepository.findByPcConfigurationId(pcConfiguration.getId());
        for (PcConfigStorage pcConfigStorage : allStoragePositions) {
            pcConfigStorageService.deletePcConfigStorage(pcConfigStorage.getConfigStorageId(), actorUserId);
        }
        PcConfigResponse pcConfigResponse = convertToResponse(pcConfiguration);
        userActivityService.addUserActivity(ActionType.BUILD_DELETE,
                "Удалена сборка «" + pcConfiguration.getBuildName() + "»", actorUserId);
        pcConfigurationRepository.deleteById(id);
        return pcConfigResponse;
    }

    public PcConfigResponse addRAMToResponse(PcConfigResponse pcConfigResponse) {
        List<PcConfigRam> configRams = pcConfigRamRepository.findByPcConfigurationId(pcConfigResponse.getId());
        List<PcConfigRamResponse> ramModulesToResponse = new ArrayList<>();
        for (PcConfigRam pcConfigRam : configRams) {
            PcConfigRamResponse pcConfigRamResponse = new PcConfigRamResponse();
            pcConfigRamResponse.setId(pcConfigRam.getConfigRamId());
            pcConfigRamResponse.setPrice(pcConfigRam.getPrice());
            pcConfigRamResponse.setQuantity(pcConfigRam.getRamQuantity());
            pcConfigRamResponse.setPcConfigurationId(pcConfigRam.getPcConfiguration().getId());
            pcConfigRamResponse.setModuleName(pcConfigRam.getRamModule().getModelMemory());
            ramModulesToResponse.add(pcConfigRamResponse);
        }

        if (!ramModulesToResponse.isEmpty()) {
            pcConfigResponse.setRamModules(ramModulesToResponse);
        }
        return pcConfigResponse;
    }

    public PcConfigResponse addStorageToResponse(PcConfigResponse pcConfigResponse) {
        List<PcConfigStorage> configStorages = pcConfigStorageRepository.findByPcConfigurationId(pcConfigResponse.getId());
        List<PcConfigStorageResponse> storageToResponse = new ArrayList<>();
        for (PcConfigStorage pcConfigStorage : configStorages) {
            PcConfigStorageResponse storageResponse = new PcConfigStorageResponse();
            storageResponse.setId(pcConfigStorage.getConfigStorageId());
            storageResponse.setPrice(pcConfigStorage.getPrice());
            storageResponse.setQuantity(pcConfigStorage.getQuantity());
            storageResponse.setPcConfigurationId(pcConfigStorage.getPcConfiguration().getId());
            storageResponse.setStorageModel(pcConfigStorage.getStorageDevice().getModel());
            storageResponse.setStorageCapacity(pcConfigStorage.getStorageDevice().getHDDCapacity());
            storageToResponse.add(storageResponse);
        }

        if (!storageToResponse.isEmpty()) {
            pcConfigResponse.setStorageDevices(storageToResponse);
        }
        return pcConfigResponse;
    }

    public PcConfigResponse getPcConfigurationById(Long id) {
        PcConfiguration pcConfiguration = pcConfigurationRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("PcConfig with id = " + id + " not found"));
        return convertToResponse(pcConfiguration);
    }

    public List<PcConfigResponse> getAllPcConfigurations() {
        return pcConfigurationRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public PcConfigResponse createPcConfiguration(PcConfigRequest pcConfigRequest, Long actorUserId) {
        if (!pcConfigRequest.getUserId().equals(actorUserId)) {
            throw new AccessDeniedException("userId in body must match authenticated user");
        }
        PcConfiguration pcConfigurationToSave = new PcConfiguration();

        pcConfigurationToSave.setBuildName(pcConfigRequest.getName());
        pcConfigurationToSave.setMotherboard(motherboardRepository.
                findById(pcConfigRequest.getMotherboardId()).orElseThrow(
                        () -> new ResourceNotFoundException("motherboard with id " +
                                pcConfigRequest.getMotherboardId() + " not found")));
        pcConfigurationToSave.setPcCase(pcCaseRepository.
                findById(pcConfigRequest.getPcCaseId()).orElseThrow(
                        () -> new ResourceNotFoundException("pc case with id " +
                                pcConfigRequest.getPcCaseId() + " not found")));
        pcConfigurationToSave.setPowerUnit(powerUnitRepository.
                findById(pcConfigRequest.getPowerUnitId()).orElseThrow(
                        () -> new ResourceNotFoundException("power unit with id " +
                                pcConfigRequest.getPowerUnitId() + " not found")));
        pcConfigurationToSave.setProcessor(processorRepository.
                findById(pcConfigRequest.getProcessorId()).orElseThrow(
                        () -> new ResourceNotFoundException("processor with id " +
                                pcConfigRequest.getProcessorId() + " not found")));
        pcConfigurationToSave.setProcessorCooling(processorCoolingRepository.
                findById(pcConfigRequest.getProcessorCoolingId()).orElseThrow(
                        () -> new ResourceNotFoundException("processor cooling with id " +
                                pcConfigRequest.getProcessorCoolingId() + " not found")));
        pcConfigurationToSave.setUser(userRepository.
                findById(pcConfigRequest.getUserId()).orElseThrow(
                        () -> new ResourceNotFoundException("user with id" +
                                pcConfigRequest.getUserId() + " not found")));
        pcConfigurationToSave.setVideoCard(videoCardRepository.
                findById(pcConfigRequest.getVideoCardId()).orElseThrow(
                        () -> new ResourceNotFoundException("videocard with id " +
                                pcConfigRequest.getVideoCardId() + " not found")));

        // без RAM и storage, они добавляются отдельными сущностями
        BigDecimal configPrice = getTotalPriceWithoutRAMAndStorage(pcConfigurationToSave);
        pcConfigurationToSave.setTotalPrice(configPrice);

        checkCompatibility(pcConfigurationToSave);
        userActivityService.addUserActivity(ActionType.BUILD_CREATE,
                "Создана сборка «" + pcConfigRequest.getName() + "»", actorUserId);
        pcConfigurationRepository.save(pcConfigurationToSave);


        return convertToResponse(pcConfigurationToSave);
    }

    public BigDecimal getTotalPriceWithoutRAMAndStorage(PcConfiguration pcConfig) {
        return pcConfig.getMotherboard().getPrice()
                .add(pcConfig.getPcCase().getPrice())
                .add(pcConfig.getPowerUnit().getPrice())
                .add(pcConfig.getProcessor().getPrice())
                .add(pcConfig.getProcessorCooling().getPrice())
                .add(pcConfig.getVideoCard().getPrice());
    }

    public PcConfigResponse convertToResponse(PcConfiguration pcConfiguration) {
        PcConfigResponse pcConfigResponse = new PcConfigResponse();

        pcConfigResponse.setId(pcConfiguration.getId());
        pcConfigResponse.setName(pcConfiguration.getBuildName());
        pcConfigResponse.setTotalPrice(pcConfiguration.getTotalPrice());
        pcConfigResponse.setMotherboardModel(pcConfiguration.getMotherboard().getModel());
        pcConfigResponse.setPcCaseModel(pcConfiguration.getPcCase().getModel());
        pcConfigResponse.setPowerUnitModel(pcConfiguration.getPowerUnit().getModel());
        pcConfigResponse.setProcessorModel(pcConfiguration.getProcessor().getModel());
        pcConfigResponse.setProcessorCoolingModel(pcConfiguration.getProcessorCooling().getModelCooling());
        pcConfigResponse.setUserName(pcConfiguration.getUser().getNickname());
        pcConfigResponse.setVideoCardModel(pcConfiguration.getVideoCard().getVideoCardModel());

        pcConfigResponse = addRAMToResponse(pcConfigResponse);
        return addStorageToResponse(pcConfigResponse);
    }

    public void checkCompatibility(PcConfiguration pcConfiguration) {

        if(!pcConfiguration.getProcessor().getSocket().equals(pcConfiguration.getMotherboard().getSocket())) {
            throw new IncompatibleComponentsException("Motherboard and processor are not compatible");
        }

        int totalTdp = compatibleService.calculateTotalTdp(pcConfiguration);
        if(totalTdp > pcConfiguration.getPowerUnit().getPower() * 0.85) {
            throw new IncompatibleComponentsException("Power unit not compatible");
        }

        if(!compatibleService.isFormFactorCompatible(pcConfiguration.getMotherboard().getFormFactor(),
                pcConfiguration.getPcCase().getFormFactor()))
        {
            throw new IncompatibleComponentsException("Form factor not compatible");
        }

        if(!compatibleService.isGpuFitsInCase(pcConfiguration.getVideoCard(), pcConfiguration.getPcCase()))
        {
            throw new IncompatibleComponentsException("Video card not compatible");
        }
    }

}





































