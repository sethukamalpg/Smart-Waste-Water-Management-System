package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.TreatmentPlantDTO;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.services.TreatmentPlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/plants")
@RequiredArgsConstructor
public class TreatmentPlantRestController {

    private final TreatmentPlantService plantService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<TreatmentPlantDTO>> getAllPlants() {
        List<TreatmentPlantDTO> plants = plantService.getAllPlants().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentPlantDTO> getPlantById(@PathVariable Long id) {
        TreatmentPlant plant = plantService.getPlantById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(plant));
    }

    @PostMapping
    public ResponseEntity<TreatmentPlantDTO> createPlant(@Valid @RequestBody TreatmentPlantDTO dto) {
        TreatmentPlant entity = dtoMapper.toEntity(dto);
        TreatmentPlant created = plantService.createPlant(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentPlantDTO> updatePlant(@PathVariable Long id, @Valid @RequestBody TreatmentPlantDTO dto) {
        TreatmentPlant entity = dtoMapper.toEntity(dto);
        TreatmentPlant updated = plantService.updatePlant(id, entity);
        return ResponseEntity.ok(dtoMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }
}
