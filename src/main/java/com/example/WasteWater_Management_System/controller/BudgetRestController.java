package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.AnalyticAccountDTO;
import com.example.WasteWater_Management_System.dto.BudgetDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.models.AnalyticAccount;
import com.example.WasteWater_Management_System.models.Budget;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.services.AccountingService;
import com.example.WasteWater_Management_System.services.TreatmentPlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/budgets")
@RequiredArgsConstructor
public class BudgetRestController {

    private final AccountingService accountingService;
    private final TreatmentPlantService plantService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getAllBudgets() {
        List<BudgetDTO> budgets = accountingService.getAllBudgets().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        Budget budget = accountingService.getBudgetById(id);
        return ResponseEntity.ok(dtoMapper.toDTO(budget));
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody BudgetDTO dto) {
        AnalyticAccount analyticAccount = null;
        if (dto.getAnalyticAccountId() != null) {
            analyticAccount = accountingService.getAnalyticAccountById(dto.getAnalyticAccountId());
        }
        Budget entity = dtoMapper.toEntity(dto, analyticAccount);
        Budget created = accountingService.createBudget(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }

    @GetMapping("/analytic-accounts")
    public ResponseEntity<List<AnalyticAccountDTO>> getAllAnalyticAccounts() {
        List<AnalyticAccountDTO> list = accountingService.getAllAnalyticAccounts().stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/analytic-accounts")
    public ResponseEntity<AnalyticAccountDTO> createAnalyticAccount(@Valid @RequestBody AnalyticAccountDTO dto) {
        TreatmentPlant plant = null;
        if (dto.getTreatmentPlantId() != null) {
            plant = plantService.getPlantById(dto.getTreatmentPlantId());
        }
        AnalyticAccount entity = dtoMapper.toEntity(dto, plant);
        AnalyticAccount created = accountingService.createAnalyticAccount(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }
}
