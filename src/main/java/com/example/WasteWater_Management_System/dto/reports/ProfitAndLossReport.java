package com.example.WasteWater_Management_System.dto.reports;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfitAndLossReport {
    private Double treatmentServiceRevenue;
    private Double complianceSurchargeRevenue;
    private Double totalRevenue;
    private Double chemicalProcurementExpense;
    private Double totalExpenses;
    private Double netProfitOrLoss;
    private Double profitMarginPercent;
}
