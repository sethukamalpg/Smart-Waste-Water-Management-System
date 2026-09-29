package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.reports.BalanceSheetReport;
import com.example.WasteWater_Management_System.dto.reports.BudgetPerformanceReport;
import com.example.WasteWater_Management_System.dto.reports.ComplianceMetricsReport;
import com.example.WasteWater_Management_System.dto.reports.ProfitAndLossReport;
import com.example.WasteWater_Management_System.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wastewater/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/balance-sheet")
    public ResponseEntity<BalanceSheetReport> getBalanceSheet() {
        return ResponseEntity.ok(reportService.getBalanceSheet());
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<ProfitAndLossReport> getProfitAndLoss() {
        return ResponseEntity.ok(reportService.getProfitAndLoss());
    }

    @GetMapping("/budget")
    public ResponseEntity<BudgetPerformanceReport> getBudgetPerformance() {
        return ResponseEntity.ok(reportService.getBudgetPerformance());
    }

    @GetMapping("/compliance")
    public ResponseEntity<ComplianceMetricsReport> getComplianceMetrics() {
        return ResponseEntity.ok(reportService.getComplianceMetrics());
    }
}
