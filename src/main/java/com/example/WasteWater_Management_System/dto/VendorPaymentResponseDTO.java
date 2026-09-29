package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPaymentResponseDTO {
    private Long id;
    private String paymentReference;
    private Long vendorBillId;
    private String billNumber;
    private String supplierName;
    private LocalDate paymentDate;
    private Double amount;
    private PaymentMethod paymentMethod;
}
