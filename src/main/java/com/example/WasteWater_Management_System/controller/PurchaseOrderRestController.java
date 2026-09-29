package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.PurchaseOrderRequestDTO;
import com.example.WasteWater_Management_System.dto.PurchaseOrderResponseDTO;
import com.example.WasteWater_Management_System.models.PurchaseOrder;
import com.example.WasteWater_Management_System.services.ProcurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderRestController {

    private final ProcurementService procurementService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> orders = procurementService.getAllPurchaseOrders().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrder po = procurementService.getPurchaseOrderById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(po));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDTO request) {
        List<ProcurementService.PurchaseOrderItemParam> itemParams = request.getItems().stream()
                .map(item -> new ProcurementService.PurchaseOrderItemParam(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .collect(Collectors.toList());

        PurchaseOrder po = procurementService.createPurchaseOrder(request.getSupplierId(), itemParams);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(po));
    }
}
