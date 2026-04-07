package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.PcConfigRamRequest;
import com.example.cursovaya.DTO.response.PcConfigRamResponse;
import com.example.cursovaya.entity.PcConfigRam;
import com.example.cursovaya.entity.PcConfiguration;
import com.example.cursovaya.entity.RamModule;
import com.example.cursovaya.enums.ActionType;
import com.example.cursovaya.exception.AccessDeniedException;
import com.example.cursovaya.exception.IncompatibleComponentsException;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.exception.ValidationException;
import com.example.cursovaya.repository.PcConfigRamRepository;
import com.example.cursovaya.repository.PcConfigurationRepository;
import com.example.cursovaya.repository.RamModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PcConfigRamService {
    private final PcConfigRamRepository pcConfigRamRepository;
    private  final RamModuleRepository ramModuleRepository;
    private final PcConfigurationRepository pcConfigurationRepository;
    private final UserActivityService userActivityService;

    private final static Logger log = Logger.getLogger(PcConfigRamService.class.getName());

    public PcConfigRamService(PcConfigRamRepository pcConfigRamRepository,
                              RamModuleRepository ramModuleRepository,
                              PcConfigurationRepository pcConfigurationRepository,
                              UserActivityService userActivityService) {
        this.pcConfigRamRepository = pcConfigRamRepository;
        this.ramModuleRepository = ramModuleRepository;
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.userActivityService = userActivityService;
    }

    @Transactional
    public PcConfigRamResponse updatePcConfigRam(Long id, PcConfigRamRequest request, Long actorUserId) {
        PcConfigRam pcConfigRam = pcConfigRamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PcConfigRam not  found with id = " + id));
        if (!pcConfigRam.getPcConfiguration().getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot modify RAM of another user's build");
        }
        BigDecimal oldPositionPrice = pcConfigRam.getPrice();
        boolean priceChanged = false;

        if (request.getRamQuantity() != null) {
            pcConfigRam.setRamQuantity(request.getRamQuantity());
            priceChanged = true;
        }
        if (request.getRamModuleId() != null) {
            RamModule newModule = ramModuleRepository.findById(request.getRamModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("module RAM not found with id = " + request.getRamModuleId()));
            pcConfigRam.setRamModule(newModule);
            priceChanged = true;
        }
        if (request.getPcConfigurationId() != null) {
            PcConfiguration newConfig = pcConfigurationRepository.findById(request.getPcConfigurationId())
                    .orElseThrow(() -> new ResourceNotFoundException("pc config not found eith id = " + request.getPcConfigurationId()));
            pcConfigRam.setPcConfiguration(newConfig);
        }
        if (priceChanged) {
            RamModule currentModule = pcConfigRam.getRamModule();
            BigDecimal newPositionPrice = currentModule.getPrice()
                    .multiply(BigDecimal.valueOf(pcConfigRam.getRamQuantity()));
            pcConfigRam.setPrice(newPositionPrice);
        }

        checkCompatibilityRam(pcConfigRam.getPcConfiguration(),
                pcConfigRam.getRamModule(), pcConfigRam.getRamQuantity());

        PcConfigRam updatedRam = pcConfigRamRepository.save(pcConfigRam);
        PcConfiguration config = updatedRam.getPcConfiguration();
        BigDecimal newTotalPrice = config.getTotalPrice().subtract(oldPositionPrice).add(pcConfigRam.getPrice());
        config.setTotalPrice(newTotalPrice);
        pcConfigurationRepository.save(config);

        log.info("updated PcConfigRam Request");

        return convertToResponse(updatedRam);
    }

    @Transactional
    public PcConfigRamResponse deletePcConfigRam(Long id, Long actorUserId) {
        PcConfigRam pcConfigRam = pcConfigRamRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("not found pcConfigRAM with id = " + id));
        if (!pcConfigRam.getPcConfiguration().getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot delete RAM of another user's build");
        }
        PcConfiguration pcConfiguration = pcConfigurationRepository.
                findById(pcConfigRam.getPcConfiguration().getId()).orElseThrow(
                () -> new ResourceNotFoundException(
                "not found pcConfig with id = " + pcConfigRam.getPcConfiguration().getId()));
        //по идеи при баге в бд тут может быть отрицательная цена
        //поправлю потом
        pcConfiguration.setTotalPrice(pcConfiguration.getTotalPrice().subtract(pcConfigRam.getPrice()));
        pcConfigurationRepository.save(pcConfiguration);
        PcConfigRamResponse pcConfigRamResponse = convertToResponse(pcConfigRam);
        pcConfigRamRepository.deleteById(id);
        return pcConfigRamResponse;
    }

    @Transactional
    public PcConfigRamResponse createPcConfigRam(PcConfigRamRequest pcConfigRam, Long actorUserId) {
        PcConfigRam pcConfigRamToSave = new PcConfigRam();

        RamModule ramModule = ramModuleRepository.findById(pcConfigRam.getRamModuleId()).
                orElseThrow(() -> new ResourceNotFoundException(
                        "module RAM not found with id = " + pcConfigRam.getRamModuleId()));

        PcConfiguration config = pcConfigurationRepository.findById(pcConfigRam.getPcConfigurationId())
                .orElseThrow(() -> new ResourceNotFoundException("Сборка не найдена"));
        if (!config.getUser().getId().equals(actorUserId)) {
            throw new AccessDeniedException("cannot add RAM to another user's build");
        }

        checkCompatibilityRam(config, ramModule, pcConfigRam.getRamQuantity());

        pcConfigRamToSave.setPrice(
                ramModule.getPrice().multiply(BigDecimal.valueOf(pcConfigRam.getRamQuantity())));
        pcConfigRamToSave.setRamQuantity(pcConfigRam.getRamQuantity());
        pcConfigRamToSave.setPcConfiguration(config);
        pcConfigRamToSave.setRamModule(ramModule);

        pcConfigRamRepository.save(pcConfigRamToSave);
        BigDecimal newTotalPrice = recountTotalPrice(pcConfigRamToSave);

        log.log(Level.INFO, "new total price: " + newTotalPrice);
        userActivityService.addUserActivity(ActionType.ADD_RAM,
                "Добавлена оперативная память к сборке «" + config.getBuildName() + "»", actorUserId);
        return convertToResponse(pcConfigRamToSave);
    }

    public BigDecimal recountTotalPrice(PcConfigRam pcConfigRam) {
        PcConfiguration pcConfiguration = pcConfigurationRepository.
                findById(pcConfigRam.getPcConfiguration().getId()).
                orElseThrow(() -> new ResourceNotFoundException(
                        "pc config not found with id = " + pcConfigRam.getPcConfiguration().getId()));
        BigDecimal totalPrice = pcConfigRam.getPrice().
                add(pcConfiguration.getTotalPrice());
        pcConfiguration.setTotalPrice(totalPrice);

        return totalPrice;
    }

    public PcConfigRamResponse convertToResponse(PcConfigRam pcConfigRam) {
        PcConfigRamResponse pcConfigRamResponse = new PcConfigRamResponse();

        pcConfigRamResponse.setId(pcConfigRam.getConfigRamId());
        pcConfigRamResponse.setModuleName(pcConfigRam.getRamModule().getModelMemory());
        pcConfigRamResponse.setPcConfigurationId(pcConfigRam.getPcConfiguration().getId());
        pcConfigRamResponse.setQuantity(pcConfigRam.getRamQuantity());
        pcConfigRamResponse.setPrice(pcConfigRam.getPrice());

        return  pcConfigRamResponse;
    }

    public PcConfigRamResponse getPcConfigRamById(Long id) {
        PcConfigRam pcConfigRam = pcConfigRamRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("PcConfigRam Not Found"));
        return convertToResponse(pcConfigRam);
    }

    public List<PcConfigRamResponse> getAllPcConfigRam() {
        return pcConfigRamRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public void checkCompatibilityRam(PcConfiguration pcConfiguration, RamModule ramModule, Integer quantity) {

        if(!ramModule.getTypeMemory().equals(pcConfiguration.getMotherboard().getSupportedRamType()))
        {
            throw  new IncompatibleComponentsException("not supported this type memory");
        }

        if(quantity < 1)
        {
            throw  new ValidationException("number of modules cannot be less than 1");
        }

        List<PcConfigRam> pcConfigRams = pcConfigRamRepository.
                findByPcConfigurationId(pcConfiguration.getId());

        Integer countModules = pcConfigRams.stream().mapToInt(PcConfigRam::getRamQuantity).sum();

        Integer totalCountModules = quantity + countModules;

        if (pcConfiguration.getMotherboard().getMaxRamSlots() <  totalCountModules) {
            throw new IncompatibleComponentsException("there are not enough slots on the motherboard");
        }
    }
}
