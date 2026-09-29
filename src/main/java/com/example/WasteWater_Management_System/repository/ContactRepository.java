package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.Contact;
import com.example.WasteWater_Management_System.models.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByContactType(ContactType contactType);
    Optional<Contact> findByName(String name);
    Optional<Contact> findByEmail(String email);
}
