package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.EffluentTelemetry;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import com.example.WasteWater_Management_System.repository.ContactRepository;
import com.example.WasteWater_Management_System.repository.EffluentTelemetryRepository;
import com.example.WasteWater_Management_System.repository.TreatmentPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final EffluentTelemetryRepository telemetryRepository;
    private final ContactRepository contactRepository;
    private final TreatmentPlantRepository treatmentPlantRepository;

    @Transactional
    public EffluentTelemetry recordTelemetry(Long factoryId, Long treatmentPlantId,
                                            Double codValue, Double phValue, Double flowRate,
                                            LocalDateTime readingDateTime) {
        Contact factory = contactRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with ID: " + factoryId));

        if (factory.getContactType() != ContactType.FACTORY) {
            throw new IllegalStateException("Contact ID " + factoryId + " is not registered as a FACTORY");
        }

        TreatmentPlant plant = treatmentPlantRepository.findById(treatmentPlantId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plant not found with ID: " + treatmentPlantId));

        double allowedCodLimit = plant.getAllowedCodLimit();
        ComplianceStatus status;
        double surchargeAmount;

        if (codValue <= allowedCodLimit) {
            status = ComplianceStatus.COMPLIANT;
            surchargeAmount = 0.0;
        } else {
            status = ComplianceStatus.NON_COMPLIANT;
            surchargeAmount = (codValue - allowedCodLimit) * 10.0;
        }

        EffluentTelemetry telemetry = EffluentTelemetry.builder()
                .factory(factory)
                .treatmentPlant(plant)
                .readingDateTime(readingDateTime != null ? readingDateTime : LocalDateTime.now())
                .codValue(codValue)
                .phValue(phValue)
                .flowRate(flowRate)
                .complianceStatus(status)
                .allowedCodLimit(allowedCodLimit)
                .surchargeAmount(surchargeAmount)
                .billed(false)
                .build();

        return telemetryRepository.save(telemetry);
    }

    public List<EffluentTelemetry> getAllTelemetry() {
        return telemetryRepository.findAll();
    }

    public EffluentTelemetry getTelemetryById(Long id) {
        return telemetryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telemetry reading not found with ID: " + id));
    }

    public List<EffluentTelemetry> getTelemetryByFactory(Long factoryId) {
        return telemetryRepository.findByFactoryId(factoryId);
    }

    public List<EffluentTelemetry> getUnbilledTelemetryByFactory(Long factoryId) {
        return telemetryRepository.findByFactoryIdAndBilledFalse(factoryId);
    }

    @Transactional
    public void markAsBilled(List<EffluentTelemetry> telemetryList) {
        for (EffluentTelemetry t : telemetryList) {
            t.setBilled(true);
            telemetryRepository.save(t);
        }
    }
}
