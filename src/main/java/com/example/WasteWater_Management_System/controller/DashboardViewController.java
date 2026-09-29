package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.reports.BalanceSheetReport;
import com.example.WasteWater_Management_System.dto.reports.BudgetPerformanceReport;
import com.example.WasteWater_Management_System.dto.reports.ComplianceMetricsReport;
import com.example.WasteWater_Management_System.dto.reports.ProfitAndLossReport;
import com.example.WasteWater_Management_System.models.*;
import com.example.WasteWater_Management_System.models.enums.*;
import com.example.WasteWater_Management_System.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final TreatmentPlantService plantService;
    private final ContactService contactService;
    private final TelemetryService telemetryService;
    private final BillingService billingService;
    private final ProcurementService procurementService;
    private final ReportService reportService;
    private final AccountingService accountingService;
    private final ProductService productService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        List<TreatmentPlant> plants = plantService.getAllPlants();
        List<Contact> factories = contactService.getContactsByType(ContactType.FACTORY);
        List<EffluentTelemetry> telemetryList = telemetryService.getAllTelemetry();
        List<CustomerInvoice> invoices = billingService.getAllInvoices();

        ComplianceMetricsReport complianceMetrics = reportService.getComplianceMetrics();
        ProfitAndLossReport pnl = reportService.getProfitAndLoss();
        BalanceSheetReport balanceSheet = reportService.getBalanceSheet();
        BudgetPerformanceReport budgetPerformance = reportService.getBudgetPerformance();

        model.addAttribute("totalPlants", plants.size());
        model.addAttribute("totalFactories", factories.size());
        model.addAttribute("complianceMetrics", complianceMetrics);
        model.addAttribute("profitAndLoss", pnl);
        model.addAttribute("balanceSheet", balanceSheet);
        model.addAttribute("budgetPerformance", budgetPerformance);

        // Recent items
        int telemetryLimit = Math.min(telemetryList.size(), 8);
        model.addAttribute("recentTelemetry", telemetryList.subList(Math.max(0, telemetryList.size() - telemetryLimit), telemetryList.size()));

        int invoiceLimit = Math.min(invoices.size(), 6);
        model.addAttribute("recentInvoices", invoices.subList(Math.max(0, invoices.size() - invoiceLimit), invoices.size()));

        return "dashboard";
    }

    @GetMapping("/plants")
    public String plantsView(Model model) {
        model.addAttribute("plants", plantService.getAllPlants());
        return "plants";
    }

    @PostMapping("/plants")
    public String createPlant(@RequestParam String name,
                              @RequestParam String location,
                              @RequestParam Double dailyCapacity,
                              @RequestParam Double allowedCodLimit,
                              @RequestParam String status,
                              RedirectAttributes redirectAttributes) {
        try {
            TreatmentPlant plant = TreatmentPlant.builder()
                    .name(name)
                    .location(location)
                    .dailyCapacity(dailyCapacity)
                    .allowedCodLimit(allowedCodLimit)
                    .status(status)
                    .build();
            plantService.createPlant(plant);
            redirectAttributes.addFlashAttribute("successMessage", "Treatment Plant '" + name + "' registered successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering plant: " + ex.getMessage());
        }
        return "redirect:/plants";
    }

    @GetMapping("/factories")
    public String factoriesView(Model model) {
        model.addAttribute("factories", contactService.getContactsByType(ContactType.FACTORY));
        model.addAttribute("suppliers", contactService.getContactsByType(ContactType.SUPPLIER));
        model.addAttribute("plants", plantService.getAllPlants());
        return "factories";
    }

    @PostMapping("/factories")
    public String createContact(@RequestParam ContactType contactType,
                                @RequestParam String name,
                                @RequestParam(required = false) Long treatmentPlantId,
                                @RequestParam(required = false) String contactPerson,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String address,
                                RedirectAttributes redirectAttributes) {
        try {
            TreatmentPlant plant = null;
            if (treatmentPlantId != null) {
                plant = plantService.getPlantById(treatmentPlantId);
            }
            Contact contact = Contact.builder()
                    .contactType(contactType)
                    .name(name)
                    .treatmentPlant(plant)
                    .contactPerson(contactPerson)
                    .email(email)
                    .phone(phone)
                    .city(city)
                    .address(address)
                    .build();
            contactService.createContact(contact);
            redirectAttributes.addFlashAttribute("successMessage", "Entity '" + name + "' saved successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving entity: " + ex.getMessage());
        }
        return "redirect:/factories";
    }

    @GetMapping("/telemetry")
    public String telemetryView(Model model) {
        model.addAttribute("telemetries", telemetryService.getAllTelemetry());
        model.addAttribute("factories", contactService.getContactsByType(ContactType.FACTORY));
        model.addAttribute("plants", plantService.getAllPlants());
        return "telemetry";
    }

    @PostMapping("/telemetry")
    public String ingestTelemetry(@RequestParam Long factoryId,
                                  @RequestParam Long treatmentPlantId,
                                  @RequestParam Double codValue,
                                  @RequestParam Double phValue,
                                  @RequestParam Double flowRate,
                                  RedirectAttributes redirectAttributes) {
        try {
            EffluentTelemetry recorded = telemetryService.recordTelemetry(
                    factoryId, treatmentPlantId, codValue, phValue, flowRate, LocalDateTime.now()
            );
            if (recorded.getComplianceStatus() == ComplianceStatus.COMPLIANT) {
                redirectAttributes.addFlashAttribute("successMessage", "Telemetry recorded: COMPLIANT (COD: " + codValue + " mg/L). No surcharge applied.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "ALERT: Telemetry NON-COMPLIANT! COD: " + codValue + " mg/L exceeded threshold. Surcharge penalty assessed: $" + recorded.getSurchargeAmount());
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error ingesting telemetry: " + ex.getMessage());
        }
        return "redirect:/telemetry";
    }

    @GetMapping({"/invoices", "/utility-billing"})
    public String invoicesView(Model model) {
        model.addAttribute("invoices", billingService.getAllInvoices());
        model.addAttribute("payments", billingService.getAllPayments());
        model.addAttribute("salesOrders", billingService.getAllSalesOrders());
        model.addAttribute("factories", contactService.getContactsByType(ContactType.FACTORY));
        model.addAttribute("products", productService.getProductsByType(ProductType.SERVICE));
        return "invoices";
    }

    @PostMapping("/invoices/sales-orders")
    public String createSalesOrder(@RequestParam Long factoryId,
                                   @RequestParam Long productId,
                                   @RequestParam Double quantity,
                                   @RequestParam Double unitPrice,
                                   RedirectAttributes redirectAttributes) {
        try {
            SalesOrder order = billingService.createSalesOrder(
                    factoryId,
                    List.of(new BillingService.OrderItemParam(productId, quantity, unitPrice))
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Service Order #" + order.getOrderNumber() + " created successfully! Subtotal: ₹" + String.format("%.2f", order.getTotalAmount()));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating service order: " + ex.getMessage());
        }
        return "redirect:/invoices";
    }

    @PostMapping("/invoices/generate")
    public String generateInvoice(@RequestParam Long salesOrderId, RedirectAttributes redirectAttributes) {
        try {
            CustomerInvoice invoice = billingService.createInvoiceFromSalesOrder(salesOrderId);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice " + invoice.getInvoiceNumber() + " successfully generated! Total: ₹" + invoice.getTotalAmount() + " (Includes ₹" + invoice.getSurchargeAmount() + " surcharges).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error generating invoice: " + ex.getMessage());
        }
        return "redirect:/invoices";
    }

    @PostMapping("/invoices/pay")
    public String processPayment(@RequestParam Long invoiceId,
                                 @RequestParam Double amount,
                                 @RequestParam PaymentMethod paymentMethod,
                                 @RequestParam(required = false) String paymentReference,
                                 RedirectAttributes redirectAttributes) {
        try {
            billingService.processCustomerPayment(invoiceId, amount, paymentMethod, paymentReference);
            redirectAttributes.addFlashAttribute("successMessage", "Payment of ₹" + amount + " posted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing payment: " + ex.getMessage());
        }
        return "redirect:/invoices";
    }

    @GetMapping("/budget")
    public String budgetView(Model model) {
        model.addAttribute("budgetPerformance", reportService.getBudgetPerformance());
        model.addAttribute("budgets", accountingService.getAllBudgets());
        model.addAttribute("analyticAccounts", accountingService.getAllAnalyticAccounts());
        model.addAttribute("plants", plantService.getAllPlants());
        model.addAttribute("pnl", reportService.getProfitAndLoss());
        model.addAttribute("vendorBills", procurementService.getAllVendorBills());
        return "budget";
    }

    @PostMapping("/budget")
    public String createBudget(@RequestParam String budgetName,
                               @RequestParam Long analyticAccountId,
                               @RequestParam String financialYear,
                               @RequestParam Double plannedAmount,
                               RedirectAttributes redirectAttributes) {
        try {
            AnalyticAccount account = accountingService.getAnalyticAccountById(analyticAccountId);
            Budget budget = Budget.builder()
                    .budgetName(budgetName)
                    .analyticAccount(account)
                    .financialYear(financialYear)
                    .plannedAmount(plannedAmount)
                    .actualAmount(0.0)
                    .status("APPROVED")
                    .build();
            accountingService.createBudget(budget);
            redirectAttributes.addFlashAttribute("successMessage", "Facility Budget '" + budgetName + "' allocated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error allocating budget: " + ex.getMessage());
        }
        return "redirect:/budget";
    }

    @GetMapping("/procurement")
    public String procurementView(Model model) {
        model.addAttribute("suppliers", contactService.getContactsByType(ContactType.SUPPLIER));
        model.addAttribute("purchaseOrders", procurementService.getAllPurchaseOrders());
        model.addAttribute("vendorBills", procurementService.getAllVendorBills());
        model.addAttribute("vendorPayments", procurementService.getAllVendorPayments());
        return "procurement";
    }

    @GetMapping("/reports/compliance")
    public String complianceReportView(Model model) {
        ComplianceMetricsReport report = reportService.getComplianceMetrics();
        List<EffluentTelemetry> violations = telemetryService.getAllTelemetry().stream()
                .filter(t -> t.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT)
                .collect(Collectors.toList());

        model.addAttribute("report", report);
        model.addAttribute("violations", violations);
        return "reports-compliance";
    }

    @GetMapping("/reports/profit-loss")
    public String profitLossReportView(Model model) {
        model.addAttribute("pnl", reportService.getProfitAndLoss());
        model.addAttribute("balanceSheet", reportService.getBalanceSheet());
        model.addAttribute("budgetPerformance", reportService.getBudgetPerformance());
        return "reports-profit-loss";
    }
}
