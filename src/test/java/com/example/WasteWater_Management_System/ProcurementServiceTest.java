package com.example.WasteWater_Management_System;

import com.example.WasteWater_Management_System.models.*;
import com.example.WasteWater_Management_System.models.enums.*;
import com.example.WasteWater_Management_System.repository.*;
import com.example.WasteWater_Management_System.services.AccountingService;
import com.example.WasteWater_Management_System.services.ProcurementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private VendorBillRepository vendorBillRepository;

    @Mock
    private VendorPaymentRepository vendorPaymentRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AccountingService accountingService;

    @InjectMocks
    private ProcurementService procurementService;

    private Contact testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = Contact.builder()
                .id(2L)
                .name("PureAqua Chemicals")
                .contactType(ContactType.SUPPLIER)
                .build();
    }

    @Test
    @DisplayName("Test Purchase Order rejects non-GOODS products (SERVICE)")
    void testRejectServiceProductInPurchaseOrder() {
        Product serviceProduct = Product.builder()
                .id(10L)
                .productName("Wastewater Treatment Consulting")
                .productType(ProductType.SERVICE)
                .unitPrice(100.0)
                .build();

        when(contactRepository.findById(2L)).thenReturn(Optional.of(testSupplier));
        when(productRepository.findById(10L)).thenReturn(Optional.of(serviceProduct));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                procurementService.createPurchaseOrder(
                        2L,
                        List.of(new ProcurementService.PurchaseOrderItemParam(10L, 5.0, 100.0))
                )
        );

        assertTrue(ex.getMessage().contains("Purchase Orders can only contain GOODS products"));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Purchase Order allows GOODS products")
    void testAllowGoodsProductInPurchaseOrder() {
        Product chemicalProduct = Product.builder()
                .id(11L)
                .productName("Chlorine Gas")
                .productType(ProductType.GOODS)
                .unitPrice(45.0)
                .build();

        when(contactRepository.findById(2L)).thenReturn(Optional.of(testSupplier));
        when(productRepository.findById(11L)).thenReturn(Optional.of(chemicalProduct));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseOrder po = procurementService.createPurchaseOrder(
                2L,
                List.of(new ProcurementService.PurchaseOrderItemParam(11L, 10.0, 45.0))
        );

        assertNotNull(po);
        assertEquals(450.0, po.getTotalAmount());
        assertEquals(OrderStatus.CONFIRMED, po.getStatus());
        assertEquals(1, po.getItems().size());
    }

    @Test
    @DisplayName("Test Vendor Bill payment overpayment rejection")
    void testVendorBillOverpaymentRejection() {
        VendorBill bill = VendorBill.builder()
                .id(50L)
                .billNumber("BILL-50")
                .supplier(testSupplier)
                .totalAmount(500.0)
                .paidAmount(200.0)
                .balanceAmount(300.0)
                .status(BillStatus.PARTIALLY_PAID)
                .build();

        when(vendorBillRepository.findById(50L)).thenReturn(Optional.of(bill));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                procurementService.processVendorPayment(50L, 350.0, PaymentMethod.BANK_TRANSFER, "VPAY-01")
        );

        assertTrue(ex.getMessage().contains("exceeds bill balance"));
        assertEquals(300.0, bill.getBalanceAmount());
        verify(vendorPaymentRepository, never()).save(any());
    }
}
