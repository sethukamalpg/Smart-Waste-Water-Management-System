package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.TreatmentPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentPlantRepository extends JpaRepository<TreatmentPlant, Long> {
    List<TreatmentPlant> findByStatus(String status);
    Optional<TreatmentPlant> findByName(String name);
}
