package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.JournalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryResponseDTO {
    private Long id;
    private LocalDate entryDate;
    private String referenceNumber;
    private String description;
    private JournalType journalType;
    private Long debitAccountId;
    private String debitAccountCode;
    private String debitAccountName;
    private Long creditAccountId;
    private String creditAccountCode;
    private String creditAccountName;
    private Double amount;
}
