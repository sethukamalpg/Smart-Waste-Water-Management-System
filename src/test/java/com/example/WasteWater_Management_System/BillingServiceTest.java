package com.example.WasteWater_Management_System;

import com.example.WasteWater_Management_System.models.*;
import com.example.WasteWater_Management_System.models.enums.*;
import com.example.WasteWater_Management_System.repository.*;
import com.example.WasteWater_Management_System.services.AccountingService;
import com.example.WasteWater_Management_System.services.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderItemRepository salesOrderItemRepository;

    @Mock
    private CustomerInvoiceRepository customerInvoiceRepository;

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @Mock
    private CustomerPaymentRepository customerPaymentRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EffluentTelemetryRepository telemetryRepository;

    @Mock
    private AccountingService accountingService;

    @InjectMocks
    private BillingService billingService;

    private CustomerInvoice testInvoice;
    private Contact testFactory;

    @BeforeEach
    void setUp() {
        testFactory = Contact.builder()
                .id(1L)
                .name("Apex Textiles Ltd")
                .contactType(ContactType.FACTORY)
                .build();

        testInvoice = CustomerInvoice.builder()
                .id(100L)
                .invoiceNumber("INV-TEST-001")
                .factory(testFactory)
                .issueDate(LocalDate.now())
                .subtotal(800.0)
                .surchargeAmount(200.0)
                .totalAmount(1000.0)
                .paidAmount(0.0)
                .balanceAmount(1000.0)
                .status(InvoiceStatus.ISSUED)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Test partial payment: Balances updated and status set to PARTIALLY_PAID")
    void testPartialPaymentSuccess() {
        when(customerInvoiceRepository.findById(100L)).thenReturn(Optional.of(testInvoice));
        when(customerPaymentRepository.save(any(CustomerPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerPayment payment = billingService.processCustomerPayment(
                100L, 400.0, PaymentMethod.BANK_TRANSFER, "REF-001"
        );

        assertNotNull(payment);
        assertEquals(400.0, payment.getAmount());
        assertEquals(400.0, testInvoice.getPaidAmount());
        assertEquals(600.0, testInvoice.getBalanceAmount());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, testInvoice.getStatus());

        verify(customerInvoiceRepository).save(testInvoice);
        verify(accountingService).recordJournalEntry(eq("REF-001"), anyString(), eq(JournalType.BANK), any(), any(), eq(400.0));
    }

    @Test
    @DisplayName("Test full payment: Balance reaches zero and status set to PAID")
    void testFullPaymentSuccess() {
        when(customerInvoiceRepository.findById(100L)).thenReturn(Optional.of(testInvoice));
        when(customerPaymentRepository.save(any(CustomerPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerPayment payment = billingService.processCustomerPayment(
                100L, 1000.0, PaymentMethod.UPI, "REF-FULL"
        );

        assertNotNull(payment);
        assertEquals(1000.0, payment.getAmount());
        assertEquals(1000.0, testInvoice.getPaidAmount());
        assertEquals(0.0, testInvoice.getBalanceAmount());
        assertEquals(InvoiceStatus.PAID, testInvoice.getStatus());

        verify(customerInvoiceRepository).save(testInvoice);
    }

    @Test
    @DisplayName("Test overpayment rejection: Should throw IllegalStateException")
    void testOverpaymentRejection() {
        when(customerInvoiceRepository.findById(100L)).thenReturn(Optional.of(testInvoice));

        // Invoice balance is 1000.0, payment attempt is 1050.0
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                billingService.processCustomerPayment(100L, 1050.0, PaymentMethod.CASH, "REF-OVER")
        );

        assertTrue(ex.getMessage().contains("exceeds outstanding balance"));
        assertEquals(0.0, testInvoice.getPaidAmount());
        assertEquals(1000.0, testInvoice.getBalanceAmount());
        verify(customerPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test non-positive payment rejection: Should throw IllegalStateException")
    void testNonPositivePaymentRejection() {
        when(customerInvoiceRepository.findById(100L)).thenReturn(Optional.of(testInvoice));

        assertThrows(IllegalStateException.class, () ->
                billingService.processCustomerPayment(100L, 0.0, PaymentMethod.CASH, "REF-ZERO")
        );

        assertThrows(IllegalStateException.class, () ->
                billingService.processCustomerPayment(100L, -50.0, PaymentMethod.CASH, "REF-NEG")
        );
    }

    @Test
    @DisplayName("Test invoice generation: Attaches pending unbilled telemetry surcharges")
    void testInvoiceGenerationWithSurcharges() {
        Product serviceProduct = Product.builder()
                .id(5L)
                .productName("Effluent Treatment")
                .unitPrice(15.0)
                .build();

        SalesOrder salesOrder = SalesOrder.builder()
                .id(20L)
                .orderNumber("SO-101")
                .factory(testFactory)
                .totalAmount(1500.0)
                .status(OrderStatus.CONFIRMED)
                .items(new ArrayList<>(List.of(
                        SalesOrderItem.builder().product(serviceProduct).quantity(100.0).unitPrice(15.0).total(1500.0).build()
                )))
                .build();

        EffluentTelemetry pendingTelemetry = EffluentTelemetry.builder()
                .id(300L)
                .factory(testFactory)
                .complianceStatus(ComplianceStatus.NON_COMPLIANT)
                .surchargeAmount(500.0)
                .billed(false)
                .build();

        when(salesOrderRepository.findById(20L)).thenReturn(Optional.of(salesOrder));
        when(telemetryRepository.findByFactoryIdAndBilledFalse(1L)).thenReturn(List.of(pendingTelemetry));
        when(customerInvoiceRepository.save(any(CustomerInvoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerInvoice generatedInvoice = billingService.createInvoiceFromSalesOrder(20L);

        assertNotNull(generatedInvoice);
        assertEquals(1500.0, generatedInvoice.getSubtotal());
        assertEquals(500.0, generatedInvoice.getSurchargeAmount());
        assertEquals(2000.0, generatedInvoice.getTotalAmount());
        assertEquals(2000.0, generatedInvoice.getBalanceAmount());
        assertEquals(InvoiceStatus.ISSUED, generatedInvoice.getStatus());
        assertTrue(pendingTelemetry.getBilled());
        assertEquals(OrderStatus.INVOICED, salesOrder.getStatus());
    }
}
