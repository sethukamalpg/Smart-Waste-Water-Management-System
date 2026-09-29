package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDTO {
    private Long id;
    private Long factoryId;
    private String factoryName;
    private Long treatmentPlantId;
    private String treatmentPlantName;
    private LocalDateTime readingDateTime;
    private Double codValue;
    private Double phValue;
    private Double flowRate;
    private ComplianceStatus complianceStatus;
    private Double allowedCodLimit;
    private Double surchargeAmount;
    private Boolean billed;
}
