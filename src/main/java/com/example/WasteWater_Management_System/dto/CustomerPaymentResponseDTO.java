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
public class CustomerPaymentResponseDTO {
    private Long id;
    private String paymentReference;
    private Long invoiceId;
    private String invoiceNumber;
    private String factoryName;
    private LocalDate paymentDate;
    private Double amount;
    private PaymentMethod paymentMethod;
}
