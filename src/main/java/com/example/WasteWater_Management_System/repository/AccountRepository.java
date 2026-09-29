package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.Account;
import com.example.WasteWater_Management_System.models.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountCode(String accountCode);
    List<Account> findByAccountType(AccountType accountType);
    List<Account> findByActive(Boolean active);
}
