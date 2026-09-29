package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.InvoiceStatus;
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
public class CustomerInvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private Long factoryId;
    private String factoryName;
    private Long salesOrderId;
    private String salesOrderNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Double subtotal;
    private Double surchargeAmount;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceAmount;
    private InvoiceStatus status;
    private List<InvoiceItemDTO> items;
}
