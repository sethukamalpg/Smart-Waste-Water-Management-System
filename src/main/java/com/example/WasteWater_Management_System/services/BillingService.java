package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.*;
import com.example.WasteWater_Management_System.models.enums.*;
import com.example.WasteWater_Management_System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final EffluentTelemetryRepository telemetryRepository;
    private final AccountingService accountingService;

    @Transactional
    public SalesOrder createSalesOrder(Long factoryId, List<OrderItemParam> itemParams) {
        Contact factory = contactRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with ID: " + factoryId));

        if (factory.getContactType() != ContactType.FACTORY) {
            throw new IllegalStateException("Contact must be of type FACTORY to create a Sales Order");
        }

        String orderNumber = "SO-" + System.currentTimeMillis();
        SalesOrder order = SalesOrder.builder()
                .orderNumber(orderNumber)
                .factory(factory)
                .orderDate(LocalDate.now())
                .totalAmount(0.0)
                .status(OrderStatus.CONFIRMED)
                .items(new ArrayList<>())
                .build();

        double total = 0.0;
        for (OrderItemParam param : itemParams) {
            Product product = productRepository.findById(param.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + param.productId()));

            double lineTotal = param.quantity() * param.unitPrice();
            SalesOrderItem item = SalesOrderItem.builder()
                    .salesOrder(order)
                    .product(product)
                    .quantity(param.quantity())
                    .unitPrice(param.unitPrice())
                    .total(lineTotal)
                    .build();

            order.getItems().add(item);
            total += lineTotal;
        }
        order.setTotalAmount(total);

        return salesOrderRepository.save(order);
    }

    @Transactional
    public CustomerInvoice createInvoiceFromSalesOrder(Long salesOrderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order not found with ID: " + salesOrderId));

        if (salesOrder.getStatus() == OrderStatus.INVOICED) {
            throw new IllegalStateException("Sales Order " + salesOrder.getOrderNumber() + " has already been invoiced");
        }

        Contact factory = salesOrder.getFactory();

        // 1. Calculate subtotal from Sales Order
        double subtotal = salesOrder.getTotalAmount();

        // 2. Fetch unbilled telemetry surcharges for this factory
        List<EffluentTelemetry> unbilledTelemetries = telemetryRepository.findByFactoryIdAndBilledFalse(factory.getId());
        double surchargeAmount = 0.0;
        for (EffluentTelemetry t : unbilledTelemetries) {
            surchargeAmount += t.getSurchargeAmount();
            t.setBilled(true);
            telemetryRepository.save(t);
        }

        double totalAmount = subtotal + surchargeAmount;
        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        CustomerInvoice invoice = CustomerInvoice.builder()
                .invoiceNumber(invoiceNumber)
                .factory(factory)
                .salesOrder(salesOrder)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .subtotal(subtotal)
                .surchargeAmount(surchargeAmount)
                .totalAmount(totalAmount)
                .paidAmount(0.0)
                .balanceAmount(totalAmount)
                .status(InvoiceStatus.ISSUED)
                .items(new ArrayList<>())
                .build();

        for (SalesOrderItem soItem : salesOrder.getItems()) {
            InvoiceItem invItem = InvoiceItem.builder()
                    .customerInvoice(invoice)
                    .product(soItem.getProduct())
                    .description("Treatment Service: " + soItem.getProduct().getProductName())
                    .quantity(soItem.getQuantity())
                    .unitPrice(soItem.getUnitPrice())
                    .total(soItem.getTotal())
                    .build();
            invoice.getItems().add(invItem);
        }

        CustomerInvoice savedInvoice = customerInvoiceRepository.save(invoice);
        salesOrder.setStatus(OrderStatus.INVOICED);
        salesOrderRepository.save(salesOrder);

        // 3. Post Double-Entry Journal Entries
        // Debit: Accounts Receivable (1020) = totalAmount
        // Credit: Effluent Treatment Fees (4010) = subtotal
        // Credit: Compliance Fines (4020) = surchargeAmount (if > 0)
        Account arAccount = accountingService.getOrCreateAccount("1020", "Accounts Receivable", AccountType.ASSET);
        Account revenueAccount = accountingService.getOrCreateAccount("4010", "Effluent Treatment Revenue", AccountType.INCOME);
        Account finesAccount = accountingService.getOrCreateAccount("4020", "Compliance Surcharges & Fines", AccountType.INCOME);

        if (subtotal > 0.0) {
            accountingService.recordJournalEntry(
                    invoiceNumber,
                    "Effluent Treatment Services - " + factory.getName(),
                    JournalType.SALES,
                    arAccount,
                    revenueAccount,
                    subtotal
            );
        }

        if (surchargeAmount > 0.0) {
            accountingService.recordJournalEntry(
                    invoiceNumber,
                    "COD Non-Compliance Surcharge Penalty - " + factory.getName(),
                    JournalType.SALES,
                    arAccount,
                    finesAccount,
                    surchargeAmount
            );
        }

        return savedInvoice;
    }

    @Transactional
    public CustomerPayment processCustomerPayment(Long invoiceId, Double amount, PaymentMethod paymentMethod, String paymentRef) {
        CustomerInvoice invoice = customerInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        if (amount <= 0.0) {
            throw new IllegalStateException("Payment amount must be greater than zero");
        }

        if (amount > invoice.getBalanceAmount()) {
            throw new IllegalStateException("Payment amount (" + amount + ") exceeds outstanding balance (" + invoice.getBalanceAmount() + ")");
        }

        double newPaidAmount = invoice.getPaidAmount() + amount;
        double newBalanceAmount = invoice.getBalanceAmount() - amount;

        invoice.setPaidAmount(newPaidAmount);
        invoice.setBalanceAmount(newBalanceAmount);

        if (newBalanceAmount == 0.0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        customerInvoiceRepository.save(invoice);

        String ref = (paymentRef != null && !paymentRef.isBlank()) ? paymentRef : "PAY-" + System.currentTimeMillis();
        CustomerPayment payment = CustomerPayment.builder()
                .paymentReference(ref)
                .invoice(invoice)
                .paymentDate(LocalDate.now())
                .amount(amount)
                .paymentMethod(paymentMethod != null ? paymentMethod : PaymentMethod.BANK_TRANSFER)
                .build();

        CustomerPayment savedPayment = customerPaymentRepository.save(payment);

        // Double-entry Journal Entry:
        // Debit: Cash/Bank (1010)
        // Credit: Accounts Receivable (1020)
        Account cashAccount = accountingService.getOrCreateAccount("1010", "Cash and Bank", AccountType.ASSET);
        Account arAccount = accountingService.getOrCreateAccount("1020", "Accounts Receivable", AccountType.ASSET);

        accountingService.recordJournalEntry(
                ref,
                "Customer Payment for " + invoice.getInvoiceNumber() + " - " + invoice.getFactory().getName(),
                JournalType.BANK,
                cashAccount,
                arAccount,
                amount
        );

        return savedPayment;
    }

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order not found with ID: " + id));
    }

    public List<CustomerInvoice> getAllInvoices() {
        return customerInvoiceRepository.findAll();
    }

    public CustomerInvoice getInvoiceById(Long id) {
        return customerInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
    }

    public List<CustomerInvoice> getInvoicesByFactory(Long factoryId) {
        return customerInvoiceRepository.findByFactoryId(factoryId);
    }

    public List<CustomerPayment> getAllPayments() {
        return customerPaymentRepository.findAll();
    }

    public List<CustomerPayment> getPaymentsByInvoice(Long invoiceId) {
        return customerPaymentRepository.findByInvoiceId(invoiceId);
    }

    public record OrderItemParam(Long productId, Double quantity, Double unitPrice) {}
}
