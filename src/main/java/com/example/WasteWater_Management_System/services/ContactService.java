package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import com.example.WasteWater_Management_System.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getContactsByType(ContactType contactType) {
        return contactRepository.findByContactType(contactType);
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
    }

    @Transactional
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Transactional
    public Contact updateContact(Long id, Contact updated) {
        Contact contact = getContactById(id);
        contact.setName(updated.getName());
        contact.setContactType(updated.getContactType());
        contact.setContactPerson(updated.getContactPerson());
        contact.setEmail(updated.getEmail());
        contact.setPhone(updated.getPhone());
        contact.setCity(updated.getCity());
        contact.setAddress(updated.getAddress());
        contact.setTreatmentPlant(updated.getTreatmentPlant());
        return contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(Long id) {
        Contact contact = getContactById(id);
        contactRepository.delete(contact);
    }
}
