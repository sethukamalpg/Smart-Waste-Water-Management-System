package com.example.WasteWater_Management_System.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_name", nullable = false)
    private String budgetName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analytic_account_id")
    private AnalyticAccount analyticAccount;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "planned_amount", nullable = false)
    private Double plannedAmount;

    @Column(name = "actual_amount", nullable = false)
    @Builder.Default
    private Double actualAmount = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private String status = "APPROVED";
}
