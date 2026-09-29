package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.CustomerInvoiceResponseDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.models.CustomerInvoice;
import com.example.WasteWater_Management_System.services.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/invoices")
@RequiredArgsConstructor
public class CustomerInvoiceRestController {

    private final BillingService billingService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<CustomerInvoiceResponseDTO>> getAllInvoices() {
        List<CustomerInvoiceResponseDTO> invoices = billingService.getAllInvoices().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerInvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        CustomerInvoice invoice = billingService.getInvoiceById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(invoice));
    }

    @GetMapping("/factory/{factoryId}")
    public ResponseEntity<List<CustomerInvoiceResponseDTO>> getInvoicesByFactory(@PathVariable Long factoryId) {
        List<CustomerInvoiceResponseDTO> invoices = billingService.getInvoicesByFactory(factoryId).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/from-sales-order/{salesOrderId}")
    public ResponseEntity<CustomerInvoiceResponseDTO> createInvoiceFromSalesOrder(@PathVariable Long salesOrderId) {
        CustomerInvoice invoice = billingService.createInvoiceFromSalesOrder(salesOrderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(invoice));
    }
}
