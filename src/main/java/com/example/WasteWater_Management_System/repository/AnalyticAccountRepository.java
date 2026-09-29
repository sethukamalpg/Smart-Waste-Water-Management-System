package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.AnalyticAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
    Optional<AnalyticAccount> findByCode(String code);
    List<AnalyticAccount> findByTreatmentPlantId(Long treatmentPlantId);
    List<AnalyticAccount> findByActive(Boolean active);
}
