package com.example.WasteWater_Management_System.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treatment_plants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentPlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "daily_capacity", nullable = false)
    private Double dailyCapacity;

    @Column(nullable = false)
    private String status;

    @Column(name = "allowed_cod_limit", nullable = false)
    private Double allowedCodLimit;
}
