package com.example.WasteWater_Management_System.controller;

import com.example.WasteWater_Management_System.dto.ContactDTO;
import com.example.WasteWater_Management_System.dto.DtoMapper;
import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import com.example.WasteWater_Management_System.services.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wastewater/suppliers")
@RequiredArgsConstructor
public class SupplierRestController {

    private final ContactService contactService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllSuppliers() {
        List<ContactDTO> suppliers = contactService.getContactsByType(ContactType.SUPPLIER).stream()
                .map(dtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getSupplierById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact.getContactType() != ContactType.SUPPLIER) {
            throw new IllegalStateException("Contact ID " + id + " is not a SUPPLIER");
        }
        return ResponseEntity.ok(dtoMapper.toDTO(contact));
    }

    @PostMapping
    public ResponseEntity<ContactDTO> createSupplier(@Valid @RequestBody ContactDTO dto) {
        dto.setContactType(ContactType.SUPPLIER);
        Contact entity = dtoMapper.toEntity(dto, null);
        Contact created = contactService.createContact(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody ContactDTO dto) {
        dto.setContactType(ContactType.SUPPLIER);
        Contact entity = dtoMapper.toEntity(dto, null);
        Contact updated = contactService.updateContact(id, entity);
        return ResponseEntity.ok(dtoMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact.getContactType() != ContactType.SUPPLIER) {
            throw new IllegalStateException("Contact ID " + id + " is not a SUPPLIER");
        }
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
