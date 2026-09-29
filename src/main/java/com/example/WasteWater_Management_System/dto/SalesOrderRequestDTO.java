package com.example.WasteWater_Management_System.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderRequestDTO {

    @NotNull(message = "Factory ID is required")
    private Long factoryId;

    @NotEmpty(message = "Order items must not be empty")
    @Valid
    private List<SalesOrderItemDTO> items;
}
