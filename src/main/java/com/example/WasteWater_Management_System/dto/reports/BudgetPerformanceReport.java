package com.example.WasteWater_Management_System.dto.reports;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BudgetPerformanceReport {
    private Double totalPlanned;
    private Double totalActual;
    private Double overallUtilizationPercent;
    private List<BudgetItemReport> items;

    @Data
    @Builder
    public static class BudgetItemReport {
        private Long budgetId;
        private String budgetName;
        private String analyticAccountName;
        private String financialYear;
        private Double plannedAmount;
        private Double actualAmount;
        private Double utilizationPercent;
        private String status;
    }
}
