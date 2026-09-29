package com.example.WasteWater_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private Double quantity;
    private Double unitPrice;
    private Double total;
}
