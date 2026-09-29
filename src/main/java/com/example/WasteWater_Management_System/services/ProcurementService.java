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
public class ProcurementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final VendorBillRepository vendorBillRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final AccountingService accountingService;

    @Transactional
    public PurchaseOrder createPurchaseOrder(Long supplierId, List<PurchaseOrderItemParam> items) {
        Contact supplier = contactRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));

        if (supplier.getContactType() != ContactType.SUPPLIER) {
            throw new IllegalStateException("Contact must be of type SUPPLIER to create a Purchase Order");
        }

        String poNumber = "PO-" + System.currentTimeMillis();
        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(poNumber)
                .supplier(supplier)
                .orderDate(LocalDate.now())
                .totalAmount(0.0)
                .status(OrderStatus.CONFIRMED)
                .items(new ArrayList<>())
                .build();

        double total = 0.0;
        for (PurchaseOrderItemParam itemParam : items) {
            Product product = productRepository.findById(itemParam.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemParam.productId()));

            // Validation: Only GOODS products allowed in Purchase Orders
            if (product.getProductType() != ProductType.GOODS) {
                throw new IllegalStateException("Purchase Orders can only contain GOODS products. Product '" +
                        product.getProductName() + "' is of type " + product.getProductType());
            }

            double lineTotal = itemParam.quantity() * itemParam.unitPrice();
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantity(itemParam.quantity())
                    .unitPrice(itemParam.unitPrice())
                    .totalAmount(lineTotal)
                    .build();

            po.getItems().add(item);
            total += lineTotal;
        }

        po.setTotalAmount(total);
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public VendorBill createVendorBillFromPO(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with ID: " + purchaseOrderId));

        if (po.getStatus() == OrderStatus.BILLED) {
            throw new IllegalStateException("Purchase Order " + po.getPoNumber() + " has already been billed");
        }

        String billNumber = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        double total = po.getTotalAmount();

        VendorBill bill = VendorBill.builder()
                .billNumber(billNumber)
                .supplier(po.getSupplier())
                .purchaseOrder(po)
                .billDate(LocalDate.now())
                .subtotal(total)
                .totalAmount(total)
                .paidAmount(0.0)
                .balanceAmount(total)
                .status(BillStatus.POSTED)
                .build();

        VendorBill savedBill = vendorBillRepository.save(bill);
        po.setStatus(OrderStatus.BILLED);
        purchaseOrderRepository.save(po);

        return savedBill;
    }

    @Transactional
    public VendorPayment processVendorPayment(Long billId, Double amount, PaymentMethod paymentMethod, String paymentRef) {
        VendorBill bill = vendorBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Bill not found with ID: " + billId));

        if (amount <= 0.0) {
            throw new IllegalStateException("Payment amount must be greater than zero");
        }

        if (amount > bill.getBalanceAmount()) {
            throw new IllegalStateException("Payment amount (" + amount + ") exceeds bill balance (" + bill.getBalanceAmount() + ")");
        }

        double newPaidAmount = bill.getPaidAmount() + amount;
        double newBalanceAmount = bill.getBalanceAmount() - amount;

        bill.setPaidAmount(newPaidAmount);
        bill.setBalanceAmount(newBalanceAmount);

        if (newBalanceAmount == 0.0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }

        vendorBillRepository.save(bill);

        String ref = (paymentRef != null && !paymentRef.isBlank()) ? paymentRef : "VPAY-" + System.currentTimeMillis();
        VendorPayment payment = VendorPayment.builder()
                .paymentReference(ref)
                .vendorBill(bill)
                .paymentDate(LocalDate.now())
                .amount(amount)
                .paymentMethod(paymentMethod != null ? paymentMethod : PaymentMethod.BANK_TRANSFER)
                .build();

        VendorPayment savedPayment = vendorPaymentRepository.save(payment);

        // Posting double-entry Journal Entry:
        // Debit: Chemical Expense (5010)
        // Credit: Cash/Bank (1010)
        Account chemicalExpense = accountingService.getOrCreateAccount("5010", "Chemical Procurement Expense", AccountType.EXPENSE);
        Account cashAccount = accountingService.getOrCreateAccount("1010", "Cash and Bank", AccountType.ASSET);

        accountingService.recordJournalEntry(
                ref,
                "Vendor Payment for Bill " + bill.getBillNumber() + " - " + bill.getSupplier().getName(),
                JournalType.PURCHASE,
                chemicalExpense,
                cashAccount,
                amount
        );

        return savedPayment;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with ID: " + id));
    }

    public List<VendorBill> getAllVendorBills() {
        return vendorBillRepository.findAll();
    }

    public VendorBill getVendorBillById(Long id) {
        return vendorBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Bill not found with ID: " + id));
    }

    public List<VendorPayment> getAllVendorPayments() {
        return vendorPaymentRepository.findAll();
    }

    public List<VendorPayment> getPaymentsByBill(Long billId) {
        return vendorPaymentRepository.findByVendorBillId(billId);
    }

    public record PurchaseOrderItemParam(Long productId, Double quantity, Double unitPrice) {}
}
