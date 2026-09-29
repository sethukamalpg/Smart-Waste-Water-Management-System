package com.example.WasteWater_Management_System.models;

import com.example.WasteWater_Management_System.models.enums.ContactType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false)
    private ContactType contactType;

    @Column(name = "contact_person")
    private String contactPerson;

    private String email;

    private String phone;

    private String city;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plant_id")
    private TreatmentPlant treatmentPlant;
}
