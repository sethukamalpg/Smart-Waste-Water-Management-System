package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.Account;
import com.example.WasteWater_Management_System.models.AnalyticAccount;
import com.example.WasteWater_Management_System.models.Budget;
import com.example.WasteWater_Management_System.models.JournalEntry;
import com.example.WasteWater_Management_System.models.enums.AccountType;
import com.example.WasteWater_Management_System.models.enums.JournalType;
import com.example.WasteWater_Management_System.repository.AccountRepository;
import com.example.WasteWater_Management_System.repository.AnalyticAccountRepository;
import com.example.WasteWater_Management_System.repository.BudgetRepository;
import com.example.WasteWater_Management_System.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountingService {

    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final BudgetRepository budgetRepository;

    public Account getAccountByCode(String code) {
        return accountRepository.findByAccountCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Account with code '" + code + "' not found"));
    }

    public Account getOrCreateAccount(String code, String defaultName, AccountType type) {
        return accountRepository.findByAccountCode(code)
                .orElseGet(() -> accountRepository.save(Account.builder()
                        .accountCode(code)
                        .accountName(defaultName)
                        .accountType(type)
                        .description(defaultName)
                        .active(true)
                        .build()));
    }

    @Transactional
    public JournalEntry recordJournalEntry(String ref, String desc, JournalType type,
                                           Account debitAccount, Account creditAccount, Double amount) {
        if (amount == null || amount <= 0.0) {
            return null;
        }
        JournalEntry entry = JournalEntry.builder()
                .entryDate(LocalDate.now())
                .referenceNumber(ref)
                .description(desc)
                .journalType(type)
                .debitAccount(debitAccount)
                .creditAccount(creditAccount)
                .amount(amount)
                .build();
        return journalEntryRepository.save(entry);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }

    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    public List<AnalyticAccount> getAllAnalyticAccounts() {
        return analyticAccountRepository.findAll();
    }

    public AnalyticAccount getAnalyticAccountById(Long id) {
        return analyticAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytic Account not found with ID: " + id));
    }

    @Transactional
    public AnalyticAccount createAnalyticAccount(AnalyticAccount analyticAccount) {
        return analyticAccountRepository.save(analyticAccount);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + id));
    }

    @Transactional
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget updateBudgetActual(Long budgetId, Double additionalActual) {
        Budget budget = getBudgetById(budgetId);
        budget.setActualAmount(budget.getActualAmount() + additionalActual);
        return budgetRepository.save(budget);
    }
}
