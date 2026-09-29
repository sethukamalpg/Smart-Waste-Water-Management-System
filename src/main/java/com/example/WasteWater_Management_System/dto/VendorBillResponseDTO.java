package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBillResponseDTO {
    private Long id;
    private String billNumber;
    private Long supplierId;
    private String supplierName;
    private Long purchaseOrderId;
    private String poNumber;
    private LocalDate billDate;
    private Double subtotal;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceAmount;
    private BillStatus status;
}
