package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.CustomerPaymentRequestDTO;
import com.example.WasteWater_Management_System.dto.CustomerPaymentResponseDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.models.CustomerPayment;
import com.example.WasteWater_Management_System.services.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/payments")
@RequiredArgsConstructor
public class CustomerPaymentRestController {

    private final BillingService billingService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<CustomerPaymentResponseDTO>> getAllPayments() {
        List<CustomerPaymentResponseDTO> payments = billingService.getAllPayments().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<CustomerPaymentResponseDTO>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<CustomerPaymentResponseDTO> payments = billingService.getPaymentsByInvoice(invoiceId).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<CustomerPaymentResponseDTO> processPayment(@Valid @RequestBody CustomerPaymentRequestDTO request) {
        CustomerPayment payment = billingService.processCustomerPayment(
                request.getInvoiceId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getPaymentReference()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(payment));
    }
}
