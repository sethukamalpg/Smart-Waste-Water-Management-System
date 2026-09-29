package com.example.WasteWater_Management_System;

import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.EffluentTelemetry;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.models.enums.ComplianceStatus;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import com.example.WasteWater_Management_System.repository.ContactRepository;
import com.example.WasteWater_Management_System.repository.EffluentTelemetryRepository;
import com.example.WasteWater_Management_System.repository.TreatmentPlantRepository;
import com.example.WasteWater_Management_System.services.TelemetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

    @Mock
    private EffluentTelemetryRepository telemetryRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private TreatmentPlantRepository treatmentPlantRepository;

    @InjectMocks
    private TelemetryService telemetryService;

    private Contact testFactory;
    private TreatmentPlant testPlant;

    @BeforeEach
    void setUp() {
        testPlant = TreatmentPlant.builder()
                .id(1L)
                .name("Central Municipal ETP")
                .location("Sector 12")
                .dailyCapacity(50000.0)
                .status("ACTIVE")
                .allowedCodLimit(250.0)
                .build();

        testFactory = Contact.builder()
                .id(10L)
                .name("Apex Textiles Ltd")
                .contactType(ContactType.FACTORY)
                .treatmentPlant(testPlant)
                .build();

        lenient().when(contactRepository.findById(10L)).thenReturn(Optional.of(testFactory));
        lenient().when(treatmentPlantRepository.findById(1L)).thenReturn(Optional.of(testPlant));
        lenient().when(telemetryRepository.save(any(EffluentTelemetry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Test COD below limit: Should be COMPLIANT with zero surcharge")
    void testCodBelowLimit() {
        EffluentTelemetry result = telemetryService.recordTelemetry(
                10L, 1L, 200.0, 7.2, 100.0, LocalDateTime.now()
        );

        assertNotNull(result);
        assertEquals(ComplianceStatus.COMPLIANT, result.getComplianceStatus());
        assertEquals(0.0, result.getSurchargeAmount(), 0.001);
        assertEquals(250.0, result.getAllowedCodLimit());
        assertEquals(200.0, result.getCodValue());
        assertFalse(result.getBilled());
    }

    @Test
    @DisplayName("Test COD at exact limit: Should be COMPLIANT with zero surcharge")
    void testCodAtExactLimit() {
        EffluentTelemetry result = telemetryService.recordTelemetry(
                10L, 1L, 250.0, 7.0, 110.0, LocalDateTime.now()
        );

        assertNotNull(result);
        assertEquals(ComplianceStatus.COMPLIANT, result.getComplianceStatus());
        assertEquals(0.0, result.getSurchargeAmount(), 0.001);
    }

    @Test
    @DisplayName("Test COD above limit: Should be NON_COMPLIANT with accurate formula surcharge")
    void testCodAboveLimitAndSurchargeFormulaAccuracy() {
        // COD = 350.0, Limit = 250.0. Excess = 100.0. Surcharge = 100.0 * 10.0 = $1,000.00
        EffluentTelemetry result = telemetryService.recordTelemetry(
                10L, 1L, 350.0, 6.2, 130.0, LocalDateTime.now()
        );

        assertNotNull(result);
        assertEquals(ComplianceStatus.NON_COMPLIANT, result.getComplianceStatus());
        assertEquals(1000.0, result.getSurchargeAmount(), 0.001);
        assertEquals(250.0, result.getAllowedCodLimit());
        assertEquals(350.0, result.getCodValue());

        ArgumentCaptor<EffluentTelemetry> captor = ArgumentCaptor.forClass(EffluentTelemetry.class);
        verify(telemetryRepository).save(captor.capture());
        assertEquals(1000.0, captor.getValue().getSurchargeAmount());
    }

    @Test
    @DisplayName("Test fractional COD surcharge formula accuracy")
    void testFractionalCodSurchargeAccuracy() {
        // COD = 265.5, Limit = 250.0. Excess = 15.5. Surcharge = 15.5 * 10.0 = $155.00
        EffluentTelemetry result = telemetryService.recordTelemetry(
                10L, 1L, 265.5, 7.8, 85.0, LocalDateTime.now()
        );

        assertNotNull(result);
        assertEquals(ComplianceStatus.NON_COMPLIANT, result.getComplianceStatus());
        assertEquals(155.0, result.getSurchargeAmount(), 0.001);
    }

    @Test
    @DisplayName("Test validation: Reject telemetry for non-FACTORY contact")
    void testRejectNonFactoryContact() {
        Contact supplier = Contact.builder()
                .id(20L)
                .name("PureChem Supplier")
                .contactType(ContactType.SUPPLIER)
                .build();
        when(contactRepository.findById(20L)).thenReturn(Optional.of(supplier));

        assertThrows(IllegalStateException.class, () ->
                telemetryService.recordTelemetry(20L, 1L, 200.0, 7.0, 50.0, LocalDateTime.now())
        );
    }
}
