package com.example.WasteWater_Management_System.dto.reports;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BalanceSheetReport {
    private Double totalAssets;
    private Double totalLiabilities;
    private Double netWorth;
    private Map<String, Double> assetBreakdown;
    private Map<String, Double> liabilityBreakdown;
}
