package com.example.WasteWater_Management_System.models;

import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "effluent_telemetries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EffluentTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id", nullable = false)
    private Contact factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plant_id", nullable = false)
    private TreatmentPlant treatmentPlant;

    @Column(name = "reading_date_time", nullable = false)
    private LocalDateTime readingDateTime;

    @Column(name = "cod_value", nullable = false)
    private Double codValue;

    @Column(name = "ph_value", nullable = false)
    private Double phValue;

    @Column(name = "flow_rate", nullable = false)
    private Double flowRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status", nullable = false)
    private ComplianceStatus complianceStatus;

    @Column(name = "allowed_cod_limit", nullable = false)
    private Double allowedCodLimit;

    @Column(name = "surcharge_amount", nullable = false)
    private Double surchargeAmount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean billed = false;
}
