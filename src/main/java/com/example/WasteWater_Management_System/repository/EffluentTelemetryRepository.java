package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.EffluentTelemetry;
import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EffluentTelemetryRepository extends JpaRepository<EffluentTelemetry, Long> {
    List<EffluentTelemetry> findByComplianceStatus(ComplianceStatus complianceStatus);
    List<EffluentTelemetry> findByFactoryId(Long factoryId);
    List<EffluentTelemetry> findByTreatmentPlantId(Long treatmentPlantId);
    List<EffluentTelemetry> findByFactoryIdAndBilledFalse(Long factoryId);
    List<EffluentTelemetry> findByBilled(Boolean billed);
    long countByComplianceStatus(ComplianceStatus complianceStatus);

    @Query("SELECT COALESCE(SUM(t.surchargeAmount), 0.0) FROM EffluentTelemetry t WHERE t.complianceStatus = :status")
    Double sumSurchargesByComplianceStatus(@Param("status") ComplianceStatus status);

    @Query("SELECT COALESCE(SUM(t.surchargeAmount), 0.0) FROM EffluentTelemetry t WHERE t.factory.id = :factoryId AND t.billed = false")
    Double sumPendingSurchargesByFactoryId(@Param("factoryId") Long factoryId);
}
