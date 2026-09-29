package com.example.WasteWater_Management_System.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analytic_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plant_id")
    private TreatmentPlant treatmentPlant;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
