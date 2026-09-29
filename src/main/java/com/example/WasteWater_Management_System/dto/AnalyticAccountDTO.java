package com.example.WasteWater_Management_System.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticAccountDTO {
    private Long id;

    @NotBlank(message = "Analytic account code is required")
    private String code;

    @NotBlank(message = "Analytic account name is required")
    private String name;

    private Long treatmentPlantId;
    private String treatmentPlantName;
    private Boolean active;
}
