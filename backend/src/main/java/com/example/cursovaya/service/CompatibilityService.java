package com.example.cursovaya.service;

import com.example.cursovaya.entity.PcCase;
import com.example.cursovaya.entity.PcConfiguration;
import com.example.cursovaya.entity.VideoCard;
import com.example.cursovaya.enums.TypeCooling;
import org.springframework.stereotype.Service;

@Service
public class CompatibilityService {
    public boolean isFormFactorCompatible(String motherboardFormFactor, String caseFormFactor) {
        String mb = motherboardFormFactor.toUpperCase().trim();
        String pcCase = caseFormFactor.toUpperCase().trim();

        return switch (mb) {
            case "ATX" -> pcCase.equals("ATX") || pcCase.equals("MID-TOWER") ||
                    pcCase.equals("FULL-TOWER") || pcCase.equals("BIG-TOWER");
            case "MATX", "MICRO-ATX", "M-ATX" -> pcCase.equals("MATX") || pcCase.equals("MICRO-ATX") ||
                    pcCase.equals("MID-TOWER") || pcCase.equals("ATX") ||
                    pcCase.equals("FULL-TOWER");
            case "ITX", "MINI-ITX" -> true;

            default -> false;
        };
    }

    public int calculateTotalTdp(PcConfiguration config) {
        int tdp = 0;
        // Процессор
        if (config.getProcessor() != null) {
            tdp += config.getProcessor().getTdp();
        }
        // Видеокарта
        if (config.getVideoCard() != null) {
            tdp += config.getVideoCard().getTdp();
        }
        // ОЗУ
        if (config.getPcConfigRams() != null) {
            tdp += config.getPcConfigRams().stream()
                    .mapToInt(ram -> 7 * ram.getRamQuantity())   // 7 Вт на модуль
                    .sum();
        }
        // Накопитель
        if (config.getPcConfigStorages() != null) {
            tdp += config.getPcConfigStorages().stream()
                    .mapToInt(storage -> 10 * storage.getQuantity())
                    .sum();
        }
        // Охлаждение
        if (config.getProcessorCooling() != null) {
            if (config.getProcessorCooling().getTypeCooling() == TypeCooling.AIO) {
                tdp += 18; // помпа + вентиляторы
            } else {
                tdp += 5;  // воздушный кулер
            }
        }
        // Материнская плата + мелкие потребители
        tdp += 30;

        return tdp;
    }

    public boolean isGpuFitsInCase(VideoCard videoCard, PcCase pcCase) {
        return videoCard.getLength() <= pcCase.getMaxGpuLength();
    }
}
