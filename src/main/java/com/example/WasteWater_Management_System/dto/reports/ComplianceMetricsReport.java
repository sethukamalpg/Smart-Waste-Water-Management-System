package com.example.WasteWater_Management_System.dto.reports;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplianceMetricsReport {
    private Long totalReadings;
    private Long compliantCount;
    private Long nonCompliantCount;
    private Double complianceRatePercent;
    private Double totalFinesAssessed;
    private Double totalFinesCollected;
    private Double pendingFines;
}
