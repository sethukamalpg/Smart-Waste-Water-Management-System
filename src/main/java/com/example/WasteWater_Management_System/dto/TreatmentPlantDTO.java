package com.example.WasteWater_Management_System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlantDTO {
    private Long id;

    @NotBlank(message = "Plant name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Daily capacity is required")
    @Positive(message = "Daily capacity must be positive")
    private Double dailyCapacity;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Allowed COD limit is required")
    @Positive(message = "Allowed COD limit must be positive")
    private Double allowedCodLimit;
}
