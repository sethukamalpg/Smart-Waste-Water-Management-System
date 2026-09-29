package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.repository.TreatmentPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentPlantService {

    private final TreatmentPlantRepository treatmentPlantRepository;

    public List<TreatmentPlant> getAllPlants() {
        return treatmentPlantRepository.findAll();
    }

    public TreatmentPlant getPlantById(Long id) {
        return treatmentPlantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment Plant not found with ID: " + id));
    }

    @Transactional
    public TreatmentPlant createPlant(TreatmentPlant plant) {
        return treatmentPlantRepository.save(plant);
    }

    @Transactional
    public TreatmentPlant updatePlant(Long id, TreatmentPlant updated) {
        TreatmentPlant plant = getPlantById(id);
        plant.setName(updated.getName());
        plant.setLocation(updated.getLocation());
        plant.setDailyCapacity(updated.getDailyCapacity());
        plant.setStatus(updated.getStatus());
        plant.setAllowedCodLimit(updated.getAllowedCodLimit());
        return treatmentPlantRepository.save(plant);
    }

    @Transactional
    public void deletePlant(Long id) {
        TreatmentPlant plant = getPlantById(id);
        treatmentPlantRepository.delete(plant);
    }
}
