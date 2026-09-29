package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.ContactDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.TreatmentPlant;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import com.example.WasteWater_Management_System.services.ContactService;
import com.example.WasteWater_Management_System.services.TreatmentPlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/factories")
@RequiredArgsConstructor
public class FactoryRestController {

    private final ContactService contactService;
    private final TreatmentPlantService plantService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllFactories() {
        List<ContactDTO> factories = contactService.getContactsByType(ContactType.FACTORY).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(factories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getFactoryById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact.getContactType() != ContactType.FACTORY) {
            throw new IllegalStateException("Contact ID " + id + " is not a FACTORY");
        }
        return ResponseEntity.ok(dtoMapper.toDTO(contact));
    }

    @PostMapping
    public ResponseEntity<ContactDTO> createFactory(@Valid @RequestBody ContactDTO dto) {
        dto.setContactType(ContactType.FACTORY);
        TreatmentPlant plant = null;
        if (dto.getTreatmentPlantId() != null) {
            plant = plantService.getPlantById(dto.getTreatmentPlantId());
        }
        Contact entity = dtoMapper.toEntity(dto, plant);
        Contact created = contactService.createContact(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateFactory(@PathVariable Long id, @Valid @RequestBody ContactDTO dto) {
        dto.setContactType(ContactType.FACTORY);
        TreatmentPlant plant = null;
        if (dto.getTreatmentPlantId() != null) {
            plant = plantService.getPlantById(dto.getTreatmentPlantId());
        }
        Contact entity = dtoMapper.toEntity(dto, plant);
        Contact updated = contactService.updateContact(id, entity);
        return ResponseEntity.ok(dtoMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactory(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact.getContactType() != ContactType.FACTORY) {
            throw new IllegalStateException("Contact ID " + id + " is not a FACTORY");
        }
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
