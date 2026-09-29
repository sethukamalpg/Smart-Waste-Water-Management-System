package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByFinancialYear(String financialYear);
    List<Budget> findByStatus(String status);
    List<Budget> findByAnalyticAccountId(Long analyticAccountId);

    @Query("SELECT COALESCE(SUM(b.plannedAmount), 0.0) FROM Budget b")
    Double sumTotalPlannedBudget();

    @Query("SELECT COALESCE(SUM(b.actualAmount), 0.0) FROM Budget b")
    Double sumTotalActualBudget();
}
