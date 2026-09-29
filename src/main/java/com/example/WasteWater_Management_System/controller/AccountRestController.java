package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.AccountDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.dto.JournalEntryResponseDTO;
import com.example.WasteWater_Management_System.models.Account;
import com.example.WasteWater_Management_System.services.AccountingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountingService accountingService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountingService.getAllAccounts().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Account account = accountingService.getAccountById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(account));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO dto) {
        Account entity = dtoMapper.toEntity(dto);
        Account created = accountingService.createAccount(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }

    @GetMapping("/journal-entries")
    public ResponseEntity<List<JournalEntryResponseDTO>> getAllJournalEntries() {
        List<JournalEntryResponseDTO> entries = accountingService.getAllJournalEntries().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(entries);
    }
}
