package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.dto.reports.BalanceSheetReport;
import com.example.WasteWater_Management_System.dto.reports.BudgetPerformanceReport;
import com.example.WasteWater_Management_System.dto.reports.ComplianceMetricsReport;
import com.example.WasteWater_Management_System.dto.reports.ProfitAndLossReport;
import com.example.WasteWater_Management_System.models.Budget;
import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import com.example.WasteWater_Management_System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final VendorBillRepository vendorBillRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final EffluentTelemetryRepository telemetryRepository;
    private final BudgetRepository budgetRepository;

    public BalanceSheetReport getBalanceSheet() {
        Double totalCustomerPayments = customerInvoiceRepository.sumTotalPaidRevenue();
        Double totalVendorPayments = vendorBillRepository.sumTotalPaidToVendors();
        Double cashAndBank = Math.max(0.0, (100000.0 + totalCustomerPayments - totalVendorPayments));

        Double accountsReceivable = customerInvoiceRepository.sumTotalOutstandingBalance();
        Double totalAssets = round(cashAndBank + accountsReceivable);

        Double accountsPayable = vendorBillRepository.sumTotalUnpaidVendorBalances();
        Double totalLiabilities = round(accountsPayable);

        Double netWorth = round(totalAssets - totalLiabilities);

        Map<String, Double> assetBreakdown = new LinkedHashMap<>();
        assetBreakdown.put("Cash and Bank", round(cashAndBank));
        assetBreakdown.put("Accounts Receivable", round(accountsReceivable));

        Map<String, Double> liabilityBreakdown = new LinkedHashMap<>();
        liabilityBreakdown.put("Accounts Payable (Chemical Suppliers)", round(accountsPayable));

        return BalanceSheetReport.builder()
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .netWorth(netWorth)
                .assetBreakdown(assetBreakdown)
                .liabilityBreakdown(liabilityBreakdown)
                .build();
    }

    public ProfitAndLossReport getProfitAndLoss() {
        Double treatmentServiceRevenue = customerInvoiceRepository.sumTotalTreatmentServiceFees();
        Double complianceSurcharges = customerInvoiceRepository.sumTotalComplianceSurcharges();
        Double totalRevenue = round(treatmentServiceRevenue + complianceSurcharges);

        Double chemicalProcurementExpense = vendorBillRepository.sumTotalBillExpenses();
        Double totalExpenses = round(chemicalProcurementExpense);

        Double netProfitOrLoss = round(totalRevenue - totalExpenses);
        Double profitMargin = totalRevenue > 0.0 ? round((netProfitOrLoss / totalRevenue) * 100.0) : 0.0;

        return ProfitAndLossReport.builder()
                .treatmentServiceRevenue(round(treatmentServiceRevenue))
                .complianceSurchargeRevenue(round(complianceSurcharges))
                .totalRevenue(totalRevenue)
                .chemicalProcurementExpense(round(chemicalProcurementExpense))
                .totalExpenses(totalExpenses)
                .netProfitOrLoss(netProfitOrLoss)
                .profitMarginPercent(profitMargin)
                .build();
    }

    public BudgetPerformanceReport getBudgetPerformance() {
        List<Budget> budgets = budgetRepository.findAll();
        double totalPlanned = 0.0;
        double totalActual = 0.0;
        List<BudgetPerformanceReport.BudgetItemReport> itemReports = new ArrayList<>();

        for (Budget b : budgets) {
            double planned = b.getPlannedAmount() != null ? b.getPlannedAmount() : 0.0;
            double actual = b.getActualAmount() != null ? b.getActualAmount() : 0.0;
            double util = planned > 0.0 ? round((actual / planned) * 100.0) : 0.0;

            totalPlanned += planned;
            totalActual += actual;

            itemReports.add(BudgetPerformanceReport.BudgetItemReport.builder()
                    .budgetId(b.getId())
                    .budgetName(b.getBudgetName())
                    .analyticAccountName(b.getAnalyticAccount() != null ? b.getAnalyticAccount().getName() : "General")
                    .financialYear(b.getFinancialYear())
                    .plannedAmount(round(planned))
                    .actualAmount(round(actual))
                    .utilizationPercent(util)
                    .status(b.getStatus())
                    .build());
        }

        double overallUtil = totalPlanned > 0.0 ? round((totalActual / totalPlanned) * 100.0) : 0.0;

        return BudgetPerformanceReport.builder()
                .totalPlanned(round(totalPlanned))
                .totalActual(round(totalActual))
                .overallUtilizationPercent(overallUtil)
                .items(itemReports)
                .build();
    }

    public ComplianceMetricsReport getComplianceMetrics() {
        long totalReadings = telemetryRepository.count();
        long compliantCount = telemetryRepository.countByComplianceStatus(ComplianceStatus.COMPLIANT);
        long nonCompliantCount = telemetryRepository.countByComplianceStatus(ComplianceStatus.NON_COMPLIANT);

        double complianceRate = totalReadings > 0
                ? round(((double) compliantCount / totalReadings) * 100.0)
                : 100.0;

        Double totalFinesAssessed = telemetryRepository.sumSurchargesByComplianceStatus(ComplianceStatus.NON_COMPLIANT);
        Double invoicedSurcharges = customerInvoiceRepository.sumTotalComplianceSurcharges();
        Double pendingFines = round(Math.max(0.0, totalFinesAssessed - invoicedSurcharges));

        return ComplianceMetricsReport.builder()
                .totalReadings(totalReadings)
                .compliantCount(compliantCount)
                .nonCompliantCount(nonCompliantCount)
                .complianceRatePercent(complianceRate)
                .totalFinesAssessed(round(totalFinesAssessed))
                .totalFinesCollected(round(invoicedSurcharges))
                .pendingFines(pendingFines)
                .build();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
