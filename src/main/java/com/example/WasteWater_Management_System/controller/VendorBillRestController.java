package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.VendorBillResponseDTO;
import com.example.WasteWater_Management_System.models.VendorBill;
import com.example.WasteWater_Management_System.services.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/vendor-bills")
@RequiredArgsConstructor
public class VendorBillRestController {

    private final ProcurementService procurementService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<VendorBillResponseDTO>> getAllVendorBills() {
        List<VendorBillResponseDTO> bills = procurementService.getAllVendorBills().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorBillResponseDTO> getVendorBillById(@PathVariable Long id) {
        VendorBill bill = procurementService.getVendorBillById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(bill));
    }

    @PostMapping("/from-purchase-order/{purchaseOrderId}")
    public ResponseEntity<VendorBillResponseDTO> createBillFromPO(@PathVariable Long purchaseOrderId) {
        VendorBill bill = procurementService.createVendorBillFromPO(purchaseOrderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(bill));
    }
}
