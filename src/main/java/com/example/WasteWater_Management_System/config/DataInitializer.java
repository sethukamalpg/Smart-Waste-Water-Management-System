package com.example.WasteWater_Management_System.config;

import com.example.WasteWater_Management_System.models.*;
import com.example.WasteWater_Management_System.models.enums.*;
import com.example.WasteWater_Management_System.repository.*;
import com.example.WasteWater_Management_System.services.AccountingService;
import com.example.WasteWater_Management_System.services.BillingService;
import com.example.WasteWater_Management_System.services.ProcurementService;
import com.example.WasteWater_Management_System.services.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TreatmentPlantRepository treatmentPlantRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final EffluentTelemetryRepository telemetryRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final BudgetRepository budgetRepository;
    private final TelemetryService telemetryService;
    private final BillingService billingService;
    private final ProcurementService procurementService;
    private final AccountingService accountingService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting WasteWater Management System Data Seeding...");

        // 1. Seed Chart of Accounts
        seedAccounts();

        // 2. Seed Treatment Plants (2 plants)
        if (treatmentPlantRepository.count() == 0) {
            seedPlantsAndIndustry();
        }

        log.info("Data Seeding completed successfully.");
    }

    private void seedAccounts() {
        if (accountRepository.count() == 0) {
            accountingService.getOrCreateAccount("1010", "Cash and Bank", AccountType.ASSET);
            accountingService.getOrCreateAccount("1020", "Accounts Receivable", AccountType.ASSET);
            accountingService.getOrCreateAccount("2010", "Accounts Payable", AccountType.LIABILITY);
            accountingService.getOrCreateAccount("4010", "Effluent Treatment Revenue", AccountType.INCOME);
            accountingService.getOrCreateAccount("4020", "Compliance Surcharges & Fines", AccountType.INCOME);
            accountingService.getOrCreateAccount("5010", "Chemical Procurement Expense", AccountType.EXPENSE);
            accountingService.getOrCreateAccount("5020", "Plant Operational Expense", AccountType.EXPENSE);
            log.info("Chart of Accounts seeded.");
        }
    }

    private void seedPlantsAndIndustry() {
        // Treatment Plants
        TreatmentPlant centralPlant = treatmentPlantRepository.save(TreatmentPlant.builder()
                .name("Central Municipal Effluent Plant")
                .location("Sector 12, River Basin District")
                .dailyCapacity(100000.0)
                .status("ACTIVE")
                .allowedCodLimit(250.0)
                .build());

        TreatmentPlant northPlant = treatmentPlantRepository.save(TreatmentPlant.builder()
                .name("North Industrial ETP")
                .location("North Corridor Industrial Hub")
                .dailyCapacity(50000.0)
                .status("ACTIVE")
                .allowedCodLimit(300.0)
                .build());

        log.info("Treatment Plants seeded: Central & North ETP.");

        // Contacts: 2 Factories, 1 Supplier
        Contact factory1 = contactRepository.save(Contact.builder()
                .name("Apex Textiles Ltd")
                .contactType(ContactType.FACTORY)
                .contactPerson("Rajesh Sharma")
                .email("rajesh.sharma@apextextiles.com")
                .phone("+91-9876543210")
                .city("Metro City")
                .address("Plot 45, Textile Park")
                .treatmentPlant(centralPlant)
                .build());

        Contact factory2 = contactRepository.save(Contact.builder()
                .name("Zenith Chemical Corp")
                .contactType(ContactType.FACTORY)
                .contactPerson("Anita Verma")
                .email("anita@zenithchem.com")
                .phone("+91-9876543211")
                .city("Metro City")
                .address("Plot 12B, North Chemical Zone")
                .treatmentPlant(northPlant)
                .build());

        Contact supplier = contactRepository.save(Contact.builder()
                .name("PureAqua Reagents & Chemicals")
                .contactType(ContactType.SUPPLIER)
                .contactPerson("Sunil Nair")
                .email("sunil@pureaqua.com")
                .phone("+91-9876543212")
                .city("Metro City")
                .address("Plot 88, Chemical Supply Hub")
                .build());

        log.info("Contacts seeded: Apex Textiles, Zenith Chemical, PureAqua.");

        // Products: Service and Goods
        Product treatmentService = productRepository.save(Product.builder()
                .productName("Effluent Treatment Service")
                .productType(ProductType.SERVICE)
                .description("Primary, Secondary, and Tertiary biological wastewater neutralization")
                .unit("m³")
                .unitPrice(15.0)
                .active(true)
                .build());

        Product chlorine = productRepository.save(Product.builder()
                .productName("Chlorine Gas (Disinfection)")
                .productType(ProductType.GOODS)
                .description("99.5% liquefied chlorine for tertiary pathogen neutralization")
                .unit("kg")
                .unitPrice(45.0)
                .active(true)
                .build());

        Product alum = productRepository.save(Product.builder()
                .productName("Coagulant Alum")
                .productType(ProductType.GOODS)
                .description("Aluminum sulphate coagulant for suspended solids clarification")
                .unit("kg")
                .unitPrice(28.0)
                .active(true)
                .build());

        log.info("Standard Products seeded.");

        // Analytic Accounts & Budgets
        AnalyticAccount centralAnalytic = analyticAccountRepository.save(AnalyticAccount.builder()
                .code("CC-ETP-01")
                .name("Central ETP Operating Cost Center")
                .treatmentPlant(centralPlant)
                .active(true)
                .build());

        AnalyticAccount northAnalytic = analyticAccountRepository.save(AnalyticAccount.builder()
                .code("CC-ETP-02")
                .name("North ETP Operating Cost Center")
                .treatmentPlant(northPlant)
                .active(true)
                .build());

        budgetRepository.save(Budget.builder()
                .budgetName("Central Plant Annual Chemical & Operations Budget")
                .analyticAccount(centralAnalytic)
                .financialYear("2026-2027")
                .plannedAmount(250000.0)
                .actualAmount(45000.0)
                .status("APPROVED")
                .build());

        budgetRepository.save(Budget.builder()
                .budgetName("North Plant Sludge Neutralization Budget")
                .analyticAccount(northAnalytic)
                .financialYear("2026-2027")
                .plannedAmount(150000.0)
                .actualAmount(28000.0)
                .status("APPROVED")
                .build());

        log.info("Analytic Cost Centers and Budgets seeded.");

        // 4 Sample Telemetry Records: 2 Compliant, 2 Non-Compliant
        // Plant 1 allowed COD: 250.0.
        // Reading 1: Compliant (COD = 210.0 <= 250.0)
        telemetryService.recordTelemetry(
                factory1.getId(), centralPlant.getId(), 210.0, 7.2, 120.0,
                LocalDateTime.now().minusHours(8)
        );

        // Reading 2: Non-Compliant (COD = 350.0 > 250.0) -> excess 100 * 10 = $1,000 surcharge
        telemetryService.recordTelemetry(
                factory1.getId(), centralPlant.getId(), 350.0, 5.8, 145.0,
                LocalDateTime.now().minusHours(6)
        );

        // Plant 2 allowed COD: 300.0.
        // Reading 3: Compliant (COD = 280.0 <= 300.0)
        telemetryService.recordTelemetry(
                factory2.getId(), northPlant.getId(), 280.0, 7.4, 95.0,
                LocalDateTime.now().minusHours(4)
        );

        // Reading 4: Non-Compliant (COD = 420.0 > 300.0) -> excess 120 * 10 = $1,200 surcharge
        telemetryService.recordTelemetry(
                factory2.getId(), northPlant.getId(), 420.0, 8.9, 110.0,
                LocalDateTime.now().minusHours(2)
        );

        log.info("4 Sample Telemetry readings seeded (2 Compliant, 2 Non-Compliant).");

        // Seed 1 Sample Sales Order and Invoice for Factory 1 to demonstrate billing cycle
        SalesOrder so1 = billingService.createSalesOrder(
                factory1.getId(),
                List.of(new BillingService.OrderItemParam(treatmentService.getId(), 200.0, 15.0)) // $3,000 service
        );
        CustomerInvoice inv1 = billingService.createInvoiceFromSalesOrder(so1.getId());
        // Process partial payment on inv1
        billingService.processCustomerPayment(inv1.getId(), 1500.0, PaymentMethod.BANK_TRANSFER, "TXN-INIT-001");

        // Seed 1 Sample Purchase Order & Vendor Bill from Supplier for Chlorine
        PurchaseOrder po1 = procurementService.createPurchaseOrder(
                supplier.getId(),
                List.of(new ProcurementService.PurchaseOrderItemParam(chlorine.getId(), 50.0, 45.0)) // $2,250 chemical
        );
        VendorBill bill1 = procurementService.createVendorBillFromPO(po1.getId());
        procurementService.processVendorPayment(bill1.getId(), 1000.0, PaymentMethod.BANK_TRANSFER, "VPAY-INIT-001");

        log.info("Initial Sales Order, Invoice, PO, and Vendor Bill seeded with partial payments.");
    }
}
