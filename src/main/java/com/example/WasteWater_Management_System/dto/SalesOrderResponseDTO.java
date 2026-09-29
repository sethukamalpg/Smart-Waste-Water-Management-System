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
public class SalesOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long factoryId;
    private String factoryName;
    private LocalDate orderDate;
    private Double totalAmount;
    private OrderStatus status;
    private List<SalesOrderItemDTO> items;
}
