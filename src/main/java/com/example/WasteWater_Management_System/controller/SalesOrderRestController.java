package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.SalesOrderRequestDTO;
import com.example.WasteWater_Management_System.dto.SalesOrderResponseDTO;
import com.example.WasteWater_Management_System.models.SalesOrder;
import com.example.WasteWater_Management_System.services.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/sales-orders")
@RequiredArgsConstructor
public class SalesOrderRestController {

    private final BillingService billingService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<SalesOrderResponseDTO>> getAllSalesOrders() {
        List<SalesOrderResponseDTO> orders = billingService.getAllSalesOrders().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponseDTO> getSalesOrderById(@PathVariable Long id) {
        SalesOrder order = billingService.getSalesOrderById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(order));
    }

    @PostMapping
    public ResponseEntity<SalesOrderResponseDTO> createSalesOrder(@Valid @RequestBody SalesOrderRequestDTO request) {
        List<BillingService.OrderItemParam> itemParams = request.getItems().stream()
                .map(item -> new BillingService.OrderItemParam(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .collect(Collectors.toList());

        SalesOrder order = billingService.createSalesOrder(request.getFactoryId(), itemParams);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(order));
    }
}
