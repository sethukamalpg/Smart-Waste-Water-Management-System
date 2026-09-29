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
public class BudgetDTO {
    private Long id;

    @NotBlank(message = "Budget name is required")
    private String budgetName;

    private Long analyticAccountId;
    private String analyticAccountName;

    @NotBlank(message = "Financial year is required")
    private String financialYear;

    @NotNull(message = "Planned amount is required")
    @Positive(message = "Planned amount must be positive")
    private Double plannedAmount;

    private Double actualAmount;
    private Double utilizationPercent;
    private String status;
}
