package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.TelemetryRequestDTO;
import com.example.WasteWater_Management_System.dto.TelemetryResponseDTO;
import com.example.WasteWater_Management_System.models.EffluentTelemetry;
import com.example.WasteWater_Management_System.services.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/telemetry")
@RequiredArgsConstructor
public class TelemetryRestController {

    private final TelemetryService telemetryService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<TelemetryResponseDTO>> getAllTelemetry() {
        List<TelemetryResponseDTO> logs = telemetryService.getAllTelemetry().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelemetryResponseDTO> getTelemetryById(@PathVariable Long id) {
        EffluentTelemetry telemetry = telemetryService.getTelemetryById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(telemetry));
    }

    @GetMapping("/factory/{factoryId}")
    public ResponseEntity<List<TelemetryResponseDTO>> getTelemetryByFactory(@PathVariable Long factoryId) {
        List<TelemetryResponseDTO> logs = telemetryService.getTelemetryByFactory(factoryId).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<TelemetryResponseDTO> ingestTelemetry(@Valid @RequestBody TelemetryRequestDTO request) {
        EffluentTelemetry recorded = telemetryService.recordTelemetry(
                request.getFactoryId(),
                request.getTreatmentPlantId(),
                request.getCodValue(),
                request.getPhValue(),
                request.getFlowRate(),
                request.getReadingDateTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(recorded));
    }
}
