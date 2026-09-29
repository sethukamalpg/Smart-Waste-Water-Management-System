package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.VendorPaymentRequestDTO;
import com.example.WasteWater_Management_System.dto.VendorPaymentResponseDTO;
import com.example.WasteWater_Management_System.models.VendorPayment;
import com.example.WasteWater_Management_System.services.ProcurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/vendor-payments")
@RequiredArgsConstructor
public class VendorPaymentRestController {

    private final ProcurementService procurementService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<VendorPaymentResponseDTO>> getAllVendorPayments() {
        List<VendorPaymentResponseDTO> payments = procurementService.getAllVendorPayments().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<VendorPaymentResponseDTO>> getPaymentsByBill(@PathVariable Long billId) {
        List<VendorPaymentResponseDTO> payments = procurementService.getPaymentsByBill(billId).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<VendorPaymentResponseDTO> processVendorPayment(@Valid @RequestBody VendorPaymentRequestDTO request) {
        VendorPayment payment = procurementService.processVendorPayment(
                request.getBillId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getPaymentReference()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(payment));
    }
}
