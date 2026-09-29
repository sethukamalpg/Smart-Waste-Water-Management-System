package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponseDTO {
    private Long id;
    private String poNumber;
    private Long supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private Double totalAmount;
    private OrderStatus status;
    private List<PurchaseOrderItemDTO> items;
}
