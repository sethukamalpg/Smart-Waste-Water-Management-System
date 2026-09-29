package com.example.WasteWater_Management_System.dto;

import com.example.WasteWater_Management_System.models.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long id;

    @NotBlank(message = "Contact name is required")
    private String name;

    @NotNull(message = "Contact type is required")
    private ContactType contactType;

    private String contactPerson;
    private String email;
    private String phone;
    private String city;
    private String address;

    private Long treatmentPlantId;
    private String treatmentPlantName;
}
