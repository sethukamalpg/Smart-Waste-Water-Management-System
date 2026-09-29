package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public TreatmentPlantDTO toDTO(TreatmentPlant plant) {
        if (plant == null) return null;
        return TreatmentPlantDTO.builder()
                .id(plant.getId())
                .name(plant.getName())
                .location(plant.getLocation())
                .dailyCapacity(plant.getDailyCapacity())
                .status(plant.getStatus())
                .allowedCodLimit(plant.getAllowedCodLimit())
                .build();
    }

    public TreatmentPlant toEntity(TreatmentPlantDTO dto) {
        if (dto == null) return null;
        return TreatmentPlant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .dailyCapacity(dto.getDailyCapacity())
                .status(dto.getStatus())
                .allowedCodLimit(dto.getAllowedCodLimit())
                .build();
    }

    public ContactDTO toDTO(Contact contact) {
        if (contact == null) return null;
        return ContactDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .contactType(contact.getContactType())
                .contactPerson(contact.getContactPerson())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .city(contact.getCity())
                .address(contact.getAddress())
                .treatmentPlantId(contact.getTreatmentPlant() != null ? contact.getTreatmentPlant().getId() : null)
                .treatmentPlantName(contact.getTreatmentPlant() != null ? contact.getTreatmentPlant().getName() : null)
                .build();
    }

    public Contact toEntity(ContactDTO dto, TreatmentPlant plant) {
        if (dto == null) return null;
        return Contact.builder()
                .id(dto.getId())
                .name(dto.getName())
                .contactType(dto.getContactType())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .address(dto.getAddress())
                .treatmentPlant(plant)
                .build();
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productType(product.getProductType())
                .description(product.getDescription())
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .active(product.getActive())
                .build();
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        return Product.builder()
                .id(dto.getId())
                .productName(dto.getProductName())
                .productType(dto.getProductType())
                .description(dto.getDescription())
                .unit(dto.getUnit())
                .unitPrice(dto.getUnitPrice())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public TelemetryResponseDTO toDTO(EffluentTelemetry telemetry) {
        if (telemetry == null) return null;
        return TelemetryResponseDTO.builder()
                .id(telemetry.getId())
                .factoryId(telemetry.getFactory() != null ? telemetry.getFactory().getId() : null)
                .factoryName(telemetry.getFactory() != null ? telemetry.getFactory().getName() : null)
                .treatmentPlantId(telemetry.getTreatmentPlant() != null ? telemetry.getTreatmentPlant().getId() : null)
                .treatmentPlantName(telemetry.getTreatmentPlant() != null ? telemetry.getTreatmentPlant().getName() : null)
                .readingDateTime(telemetry.getReadingDateTime())
                .codValue(telemetry.getCodValue())
                .phValue(telemetry.getPhValue())
                .flowRate(telemetry.getFlowRate())
                .complianceStatus(telemetry.getComplianceStatus())
                .allowedCodLimit(telemetry.getAllowedCodLimit())
                .surchargeAmount(telemetry.getSurchargeAmount())
                .billed(telemetry.getBilled())
                .build();
    }

    public SalesOrderResponseDTO toDTO(SalesOrder order) {
        if (order == null) return null;
        List<SalesOrderItemDTO> itemDTOs = order.getItems() != null
                ? order.getItems().stream().map(this::toDTO).collect(Collectors.toList())
                : Collections.emptyList();

        return SalesOrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .factoryId(order.getFactory() != null ? order.getFactory().getId() : null)
                .factoryName(order.getFactory() != null ? order.getFactory().getName() : null)
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemDTOs)
                .build();
    }

    public SalesOrderItemDTO toDTO(SalesOrderItem item) {
        if (item == null) return null;
        return SalesOrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getProductName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .build();
    }

    public CustomerInvoiceResponseDTO toDTO(CustomerInvoice invoice) {
        if (invoice == null) return null;
        List<InvoiceItemDTO> itemDTOs = invoice.getItems() != null
                ? invoice.getItems().stream().map(this::toDTO).collect(Collectors.toList())
                : Collections.emptyList();

        return CustomerInvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .factoryId(invoice.getFactory() != null ? invoice.getFactory().getId() : null)
                .factoryName(invoice.getFactory() != null ? invoice.getFactory().getName() : null)
                .salesOrderId(invoice.getSalesOrder() != null ? invoice.getSalesOrder().getId() : null)
                .salesOrderNumber(invoice.getSalesOrder() != null ? invoice.getSalesOrder().getOrderNumber() : null)
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .surchargeAmount(invoice.getSurchargeAmount())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .items(itemDTOs)
                .build();
    }

    public InvoiceItemDTO toDTO(InvoiceItem item) {
        if (item == null) return null;
        return InvoiceItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getProductName() : null)
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .build();
    }

    public CustomerPaymentResponseDTO toDTO(CustomerPayment payment) {
        if (payment == null) return null;
        return CustomerPaymentResponseDTO.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .invoiceId(payment.getInvoice() != null ? payment.getInvoice().getId() : null)
                .invoiceNumber(payment.getInvoice() != null ? payment.getInvoice().getInvoiceNumber() : null)
                .factoryName(payment.getInvoice() != null && payment.getInvoice().getFactory() != null ? payment.getInvoice().getFactory().getName() : null)
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

    public PurchaseOrderResponseDTO toDTO(PurchaseOrder po) {
        if (po == null) return null;
        List<PurchaseOrderItemDTO> itemDTOs = po.getItems() != null
                ? po.getItems().stream().map(this::toDTO).collect(Collectors.toList())
                : Collections.emptyList();

        return PurchaseOrderResponseDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier() != null ? po.getSupplier().getId() : null)
                .supplierName(po.getSupplier() != null ? po.getSupplier().getName() : null)
                .orderDate(po.getOrderDate())
                .totalAmount(po.getTotalAmount())
                .status(po.getStatus())
                .items(itemDTOs)
                .build();
    }

    public PurchaseOrderItemDTO toDTO(PurchaseOrderItem item) {
        if (item == null) return null;
        return PurchaseOrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getProductName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalAmount(item.getTotalAmount())
                .build();
    }

    public VendorBillResponseDTO toDTO(VendorBill bill) {
        if (bill == null) return null;
        return VendorBillResponseDTO.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .supplierId(bill.getSupplier() != null ? bill.getSupplier().getId() : null)
                .supplierName(bill.getSupplier() != null ? bill.getSupplier().getName() : null)
                .purchaseOrderId(bill.getPurchaseOrder() != null ? bill.getPurchaseOrder().getId() : null)
                .poNumber(bill.getPurchaseOrder() != null ? bill.getPurchaseOrder().getPoNumber() : null)
                .billDate(bill.getBillDate())
                .subtotal(bill.getSubtotal())
                .totalAmount(bill.getTotalAmount())
                .paidAmount(bill.getPaidAmount())
                .balanceAmount(bill.getBalanceAmount())
                .status(bill.getStatus())
                .build();
    }

    public VendorPaymentResponseDTO toDTO(VendorPayment payment) {
        if (payment == null) return null;
        return VendorPaymentResponseDTO.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .vendorBillId(payment.getVendorBill() != null ? payment.getVendorBill().getId() : null)
                .billNumber(payment.getVendorBill() != null ? payment.getVendorBill().getBillNumber() : null)
                .supplierName(payment.getVendorBill() != null && payment.getVendorBill().getSupplier() != null ? payment.getVendorBill().getSupplier().getName() : null)
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

    public AccountDTO toDTO(Account account) {
        if (account == null) return null;
        return AccountDTO.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .description(account.getDescription())
                .active(account.getActive())
                .build();
    }

    public Account toEntity(AccountDTO dto) {
        if (dto == null) return null;
        return Account.builder()
                .id(dto.getId())
                .accountCode(dto.getAccountCode())
                .accountName(dto.getAccountName())
                .accountType(dto.getAccountType())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public JournalEntryResponseDTO toDTO(JournalEntry entry) {
        if (entry == null) return null;
        return JournalEntryResponseDTO.builder()
                .id(entry.getId())
                .entryDate(entry.getEntryDate())
                .referenceNumber(entry.getReferenceNumber())
                .description(entry.getDescription())
                .journalType(entry.getJournalType())
                .debitAccountId(entry.getDebitAccount() != null ? entry.getDebitAccount().getId() : null)
                .debitAccountCode(entry.getDebitAccount() != null ? entry.getDebitAccount().getAccountCode() : null)
                .debitAccountName(entry.getDebitAccount() != null ? entry.getDebitAccount().getAccountName() : null)
                .creditAccountId(entry.getCreditAccount() != null ? entry.getCreditAccount().getId() : null)
                .creditAccountCode(entry.getCreditAccount() != null ? entry.getCreditAccount().getAccountCode() : null)
                .creditAccountName(entry.getCreditAccount() != null ? entry.getCreditAccount().getAccountName() : null)
                .amount(entry.getAmount())
                .build();
    }

    public AnalyticAccountDTO toDTO(AnalyticAccount analyticAccount) {
        if (analyticAccount == null) return null;
        return AnalyticAccountDTO.builder()
                .id(analyticAccount.getId())
                .code(analyticAccount.getCode())
                .name(analyticAccount.getName())
                .treatmentPlantId(analyticAccount.getTreatmentPlant() != null ? analyticAccount.getTreatmentPlant().getId() : null)
                .treatmentPlantName(analyticAccount.getTreatmentPlant() != null ? analyticAccount.getTreatmentPlant().getName() : null)
                .active(analyticAccount.getActive())
                .build();
    }

    public AnalyticAccount toEntity(AnalyticAccountDTO dto, TreatmentPlant plant) {
        if (dto == null) return null;
        return AnalyticAccount.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .treatmentPlant(plant)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public BudgetDTO toDTO(Budget budget) {
        if (budget == null) return null;
        double planned = budget.getPlannedAmount() != null ? budget.getPlannedAmount() : 0.0;
        double actual = budget.getActualAmount() != null ? budget.getActualAmount() : 0.0;
        double util = planned > 0.0 ? (actual / planned) * 100.0 : 0.0;

        return BudgetDTO.builder()
                .id(budget.getId())
                .budgetName(budget.getBudgetName())
                .analyticAccountId(budget.getAnalyticAccount() != null ? budget.getAnalyticAccount().getId() : null)
                .analyticAccountName(budget.getAnalyticAccount() != null ? budget.getAnalyticAccount().getName() : null)
                .financialYear(budget.getFinancialYear())
                .plannedAmount(planned)
                .actualAmount(actual)
                .utilizationPercent(Math.round(util * 100.0) / 100.0)
                .status(budget.getStatus())
                .build();
    }

    public Budget toEntity(BudgetDTO dto, AnalyticAccount analyticAccount) {
        if (dto == null) return null;
        return Budget.builder()
                .id(dto.getId())
                .budgetName(dto.getBudgetName())
                .analyticAccount(analyticAccount)
                .financialYear(dto.getFinancialYear())
                .plannedAmount(dto.getPlannedAmount())
                .actualAmount(dto.getActualAmount() != null ? dto.getActualAmount() : 0.0)
                .status(dto.getStatus() != null ? dto.getStatus() : "APPROVED")
                .build();
    }
}
