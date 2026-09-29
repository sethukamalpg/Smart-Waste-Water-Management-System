package com.example.WasteWater_Management_System.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRequestDTO {

    @NotNull(message = "Factory ID is required")
    private Long factoryId;

    @NotNull(message = "Treatment Plant ID is required")
    private Long treatmentPlantId;

    @NotNull(message = "COD value is required")
    @Positive(message = "COD value must be positive")
    private Double codValue;

    @NotNull(message = "pH value is required")
    private Double phValue;

    @NotNull(message = "Flow rate is required")
    @Positive(message = "Flow rate must be positive")
    private Double flowRate;

    private LocalDateTime readingDateTime;
}
